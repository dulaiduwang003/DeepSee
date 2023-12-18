package com.cn.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cn.common.DallCommon;
import com.cn.constant.DrawingConstant;
import com.cn.constant.DrawingStatusConstant;
import com.cn.dto.DallTaskDto;
import com.cn.dto.DialogueImageDto;
import com.cn.entity.TsDialogueDrawing;
import com.cn.entity.TsGenerateDrawing;
import com.cn.enums.DrawingTypeEnum;
import com.cn.enums.FileEnum;
import com.cn.exception.DallException;
import com.cn.mapper.TsDialogueDrawingMapper;
import com.cn.mapper.TsGenerateDrawingMapper;
import com.cn.model.DallModel;
import com.cn.model.DialogueGenerateModel;
import com.cn.service.DallService;
import com.cn.structure.TaskStructure;
import com.cn.utils.BaiduTranslationUtil;
import com.cn.utils.DrawingUtils;
import com.cn.utils.RedisUtils;
import com.cn.utils.UploadUtil;
import com.cn.vo.DialogueImageVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DallServiceImpl implements DallService {

    private final BaiduTranslationUtil baiduTranslationUtil;

    private final TsDialogueDrawingMapper tsDialogueDrawingMapper;

    private final TsGenerateDrawingMapper tsGenerateDrawingMapper;

    private final UploadUtil uploadUtil;

    private final DrawingUtils drawingUtils;

    private final RedisUtils redisUtils;

    private final RedisTemplate<String, Object> redisTemplate;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public DialogueImageVo dialogGenerationImg(final DialogueImageDto dto) {

        final String block = WebClient.builder().baseUrl(DallCommon.STRUCTURE.getRequestUrl()).defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + DallCommon.pollGetKey()).codecs(item -> item.defaultCodecs().maxInMemorySize(20 * 1024 * 1024)).build().post().uri("/images/generations").body(BodyInserters.fromValue(new DialogueGenerateModel().setPrompt(dto.getPrompt()))).retrieve().onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> response.bodyToMono(String.class).flatMap(errorBody -> {
            final String errorCode = JSONObject.parseObject(errorBody).getString("error");
            final JSONObject jsonObject = JSONObject.parseObject(errorCode);
            final String code = jsonObject.getString("code");
            if ("rate_limit_exceeded".equals(code)) {
                log.warn("DALL-3 已经超过官方限制速率");
                return Mono.error(new DallException("🥲 Sorry! 当前绘图人数过多,请稍后重试~"));
            }
            return Mono.error(new DallException("🥲 Sorry! 当前对话绘图服务可能出了点问题,请联系管理员解决~"));
        })).bodyToMono(String.class).block();
        //解析JSON
        final JSONObject jsonObject = JSONObject.parseObject(block);
        final JSONObject data = jsonObject.getJSONArray("data").getJSONObject(0);
        String revisedPrompt = data.getString("revised_prompt");
        //上传数据到阿里云OSS 不然回显过慢
        final String url = uploadUtil.uploadImageFromUrl(data.getString("url"), FileEnum.DRAWING.getDec());
        tsDialogueDrawingMapper.insert(new TsDialogueDrawing().setUrl(url));
        synchronized (this) {
            try {
                //百度翻译API 单 1秒qs
                revisedPrompt = baiduTranslationUtil.chineseTranslation(revisedPrompt);
            } catch (Exception e) {
                log.warn("调取百度翻译API失败 信息:{} 位置:{}", e.getMessage(), e.getClass());
            }
        }
        return new DialogueImageVo().setRevisedPrompt(revisedPrompt).setUrl(url);

    }

    @Override
    public String addDallTask(final DallTaskDto dto) {
        final Long currentLoginId = drawingUtils.verifyTask();
        //生成任务标识
        final String taskId = String.valueOf(UUID.randomUUID());
        final String actualTaskId = DrawingConstant.TASK + taskId;
        //设置个人任务队列
        final TaskStructure taskStructure = new TaskStructure()
                //设置任务状态为 构件中
                .setTaskId(taskId).setStatus(DrawingStatusConstant.PENDING).setDrawingType(DrawingTypeEnum.DALL.getDec()).setPrompt(dto.getPrompt());
        //设置个人任务
        redisUtils.setValueTimeout(actualTaskId, taskStructure, 1800);
        //填充模型
        final DallModel dallModel = new DallModel().setSize(dto.getSize()).setMask(dto.getMask()).setPrompt(dto.getPrompt()).setImage(dto.getImage());
        tsGenerateDrawingMapper.insert(new TsGenerateDrawing().setStatus(DrawingStatusConstant.PENDING).setPrompt(dto.getPrompt()).setGenerateDrawingId(taskId).setType(DrawingTypeEnum.DALL.getDec()).setUserId(currentLoginId));
        //加入任务队列
        redisTemplate.opsForList().leftPush(DrawingConstant.DALL_TASK_QUEUE, taskStructure.setExtra(dallModel));
        return taskId;
    }
}
