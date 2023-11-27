package com.cn.listener;

import com.alibaba.fastjson.JSONObject;
import com.cn.common.SdCommon;
import com.cn.common.structure.TaskStructure;
import com.cn.constant.DrawingConstant;
import com.cn.constant.DrawingStatusConstant;
import com.cn.entity.TsDrawing;
import com.cn.enums.FileEnum;
import com.cn.enums.TaskEnum;
import com.cn.mapper.TsDrawingMapper;
import com.cn.model.SdModel;
import com.cn.utils.UploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class DrawingTaskListener {

    private final RedisTemplate<String, Object> redisTemplate;

    private final WebClient.Builder webClient;
    private final Semaphore semaphore = new Semaphore(1);

    private final ThreadPoolExecutor threadPoolExecutor;


    private final UploadUtil uploadUtil;

    private final TsDrawingMapper tsDrawingMapper;

    @EventListener(ApplicationReadyEvent.class)
    @SuppressWarnings("all")
    public void sdListening() {
        threadPoolExecutor.execute(() -> {
            while (true) {
                try {
                    semaphore.acquire();
                    //每三秒从队列中获取数据
                    final TaskStructure ts = (TaskStructure) redisTemplate.opsForList().rightPop(DrawingConstant.DRAWING_TASK_QUEUE, 2, TimeUnit.SECONDS);
                    if (ts != null) {
                        //根据不同模型分配任务
                        switch (ts.getTaskEnum()) {
                            case SD_GENERATE -> {
                                this.handleSdGenerate(ts.getExtra(), ts.getTaskId());
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    log.error("绘图异常 原因:{} 位置:{}", e.getMessage(), e.getClass());
                } finally {
                    semaphore.release(); // 释放信号量许可
                }

            }
        });
    }


    private void handleSdGenerate(Object o, String taskId) {

        final SdModel model = (SdModel) o;
        //提取出上传图片
        final List<String> initImages = model.getInit_images();
        //路由
        String router;
        if (initImages != null && !initImages.isEmpty()) {
            router = "/img2img";
        } else {
            router = "/txt2img";
        }
        //设置执行任务
        try {
            redisTemplate.opsForValue().set(DrawingConstant.DRAWING_EXECUTION, taskId);
            //状态设置为 构建中
            final String block = webClient.baseUrl(SdCommon.STRUCTURE.getRequestUrl())
                    .codecs(item -> item.defaultCodecs().maxInMemorySize(20 * 1024 * 1024)).build()
                    .post()
                    .uri(router)
                    .body(BodyInserters.fromValue(model))
                    .retrieve()
                    .bodyToMono(String.class).block();
            //获取到指定图片 SD给的是BASE64数据
            final String base64 = JSONObject.parseObject(block).getJSONArray("images").getString(0);
            //上传至阿里云
            final String drawingUrl = uploadUtil.uploadBase64Image(base64, FileEnum.DRAWING.getDec());
            //上传至队列
            tsDrawingMapper.insert(new TsDrawing().setUrl(drawingUrl));
            //设置构建成功
            redisTemplate.opsForValue().set(taskId, new TaskStructure()
                    .setTaskEnum(TaskEnum.SD_GENERATE)
                    .setStatus(DrawingStatusConstant.BUILD_SUCCEED)
                    .setTaskId(taskId)
                    .setPrompt(model.getPrompt())
                    .setImageUrl(drawingUrl), 1800000, TimeUnit.SECONDS);
            //移除当前执行队列 进入空等待 释放资源
            redisTemplate.delete(DrawingConstant.DRAWING_EXECUTION);

        } catch (Exception e) {
            e.printStackTrace();
            //设置返回错误结果
            redisTemplate.opsForValue()
                    .set(taskId, new TaskStructure()
                            .setTaskEnum(TaskEnum.SD_GENERATE)
                            .setStatus(DrawingStatusConstant.BUILD_FAIL)
                            .setPrompt(model.getPrompt())
                            .setTaskId(taskId), 1800000, TimeUnit.SECONDS);
        }

    }


}
