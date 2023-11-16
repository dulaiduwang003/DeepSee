package com.cn.listener;

import com.alibaba.fastjson.JSONObject;
import com.cn.common.DallCommon;
import com.cn.common.SdCommon;
import com.cn.common.structure.TaskStructure;
import com.cn.constant.DrawingConstant;
import com.cn.constant.DrawingStatusConstant;
import com.cn.entity.TsDrawing;
import com.cn.enums.FileEnum;
import com.cn.enums.TaskEnum;
import com.cn.mapper.TsDrawingMapper;
import com.cn.model.DallModel;
import com.cn.model.SdModel;
import com.cn.utils.BaiduTranslationUtil;
import com.cn.utils.UploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
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

    private final BaiduTranslationUtil translationUtil;

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
                    final TaskStructure ts = (TaskStructure) redisTemplate.opsForList().rightPop(DrawingConstant.DRAWING_TASK_QUEUE, 3, TimeUnit.SECONDS);
                    if (ts != null) {
                        //根据不同模型分配任务
                        switch (ts.getTaskEnum()) {
                            case DALL -> {
                                this.handleDall(ts.getPrompt(), ts.getTaskId());
                            }
                            case SD -> {
                                this.handleSd(ts.getExtra(), ts.getTaskId());
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

    private void handleDall(String prompt, final String taskId) {
        final String block;
        try {
            block = webClient.baseUrl(DallCommon.STRUCTURE.getRequestUrl()).defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + DallCommon.pollGetKey()).build().post().uri("/images/generations").body(BodyInserters.fromValue(new DallModel().setPrompt(prompt))).retrieve().bodyToMono(String.class).block();
        } catch (Exception e) {
            //设置返回错误结果
            redisTemplate.opsForValue().set(taskId, new TaskStructure().setTaskEnum(TaskEnum.DALL).setStatus(DrawingStatusConstant.BUILD_FAIL).setPrompt(prompt).setTaskId(taskId), 600000, TimeUnit.SECONDS);
            return;
        }
        //转为JSON
        final JSONObject jsonObject = JSONObject.parseObject(block);
        final JSONObject data = jsonObject.getJSONArray("data").getJSONObject(0);
        String entry = null;
        try {
            final String revisedPrompt = data.getString("revised_prompt");
            entry = translationUtil.chineseTranslation(revisedPrompt);
        } catch (Exception e) {
            log.warn("百度翻译失效 原因:{} 位置:{}", e.getMessage(), e.getClass());
        }
        //更新REDIS任务节点 设置过期时间为 30分钟 ,另外这里要监听这些队列中已经过期的值 并做出相应的回应
        final String drawingUrl = uploadUtil.uploadImageFromUrl(data.getString("url"), FileEnum.DRAWING.getDec());
        //上传至队列
        tsDrawingMapper.insert(new TsDrawing().setUrl(drawingUrl));
        redisTemplate.opsForValue().set(taskId, new TaskStructure().setTaskEnum(TaskEnum.DALL).setStatus(DrawingStatusConstant.BUILD_SUCCEED).setTaskId(taskId).setPrompt(prompt).setImageUrl(drawingUrl)
                //设置额外的参数
                .setExtra(entry), 600000, TimeUnit.SECONDS);


    }

    private void handleSd(Object o, String taskId) {
        final SdModel model = (SdModel) o;
        //提取出上传图片
        final List<String> initImages = model.getInit_images();
        //路由
        String router;
        if (!initImages.isEmpty()) {
            router = "/img2img";
        } else {
            router = "/txt2img";
        }
        try {

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
                    .setTaskEnum(TaskEnum.SD)
                    .setStatus(DrawingStatusConstant.BUILD_SUCCEED)
                    .setTaskId(taskId)
                    .setPrompt(model.getPrompt())
                    .setImageUrl(drawingUrl)
                    //SD 没有任何参数 设置为NULL
                    .setExtra(null), 600000, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            //设置返回错误结果
            redisTemplate.opsForValue().set(taskId, new TaskStructure().setTaskEnum(TaskEnum.DALL).setStatus(DrawingStatusConstant.BUILD_FAIL).setPrompt(model.getPrompt()).setTaskId(taskId), 600000, TimeUnit.SECONDS);
        }

    }

}
