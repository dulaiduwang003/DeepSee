package com.cn.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cn.common.SdCommon;
import com.cn.common.structure.SdStructure;
import com.cn.common.structure.TaskStructure;
import com.cn.constant.DrawingConstant;
import com.cn.constant.DrawingStatusConstant;
import com.cn.dto.SdTaskDto;
import com.cn.enums.TaskEnum;
import com.cn.model.SdModel;
import com.cn.service.SdService;
import com.cn.utils.RedisUtils;
import com.cn.utils.StringUtils;
import com.cn.vo.SdParamVo;
import com.cn.vo.TaskResultVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SdServiceImpl implements SdService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisUtils redisUtils;

    @Override
    public SdParamVo getSdParam() {
        final SdStructure config = SdCommon.getConfig();
        return new SdParamVo().setSamplerList(config.getSamplerList()).setSteps(config.getSteps()).setModelList(config.getModelList());
    }

    @Override
    public String addSdDrawingTask(final SdTaskDto dto) {
        final String taskId = String.valueOf(UUID.randomUUID());
        final String actualTaskId = DrawingConstant.DRAWING_TASK + taskId;
        //设置个人任务队列
        final TaskStructure taskStructure = new TaskStructure()
                //设置任务状态为 构件中
                .setTaskId(actualTaskId).setStatus(DrawingStatusConstant.BUILDING).setTaskEnum(TaskEnum.SD_GENERATE).setPrompt(dto.getPrompt());
        //设置个人任务
        redisUtils.setValueTimeout(actualTaskId, taskStructure, 3600000);
        //判断是否上传图片
        final SdModel sdModel = new SdModel().setPrompt(dto.getPrompt()).setSteps(dto.getSteps()).setWidth(dto.getWidth()).setHeight(dto.getHeight()).setOverride_settings(new SdModel.Override()
                //设置动态模型昵称
                .setSd_model_checkpoint(dto.getModelName())).setSampler_index(dto.getSampler_index()).setNegative_prompt(dto.getNegative_prompt());
        if (StringUtils.notEmpty(dto.getImages())) {
            sdModel.setInit_images(List.of(dto.getImages()));
        }

        redisTemplate.opsForList().leftPush(DrawingConstant.DRAWING_TASK_QUEUE, taskStructure.setExtra(sdModel));
        return taskId;
    }


    @Override
    public TaskResultVo getDrawingTask(String taskId) {
        taskId = DrawingConstant.DRAWING_TASK + taskId;
        if (redisUtils.doesItExist(taskId)) {
            final TaskStructure taskStructure = (TaskStructure) redisUtils.getValue(taskId);
            //判断当前处理的是否为 当前task
            final String handleTaskId = String.valueOf(redisUtils.getValue(DrawingConstant.DRAWING_EXECUTION));
            //验证状态
            if (taskId.equals(handleTaskId) && Objects.equals(taskStructure.getStatus(), DrawingStatusConstant.BUILDING)) {
                //实时获取数据
                try {
                    final JSONObject jsonObject = getProgressFromWebClient();
                    //计算距离完成百分比
                    int percentage = calculatePercentage(jsonObject);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("progress", percentage);
                    map.put("current_image", jsonObject.getString("current_image"));
                    return new TaskResultVo().setPrompt(taskStructure.getPrompt())
                            //预览效果
                            .setStatus(DrawingStatusConstant.PREVIEW).setExtra(map);
                } catch (Exception e) {
                    return new TaskResultVo().setPrompt(taskStructure.getPrompt())
                            //预览效果
                            .setStatus(DrawingStatusConstant.PREVIEW);
                }


            } else {
                return handleTaskStatus(taskStructure);
            }
        }
        //任务已被抛弃 或不存在
        return null;
    }

    private JSONObject getProgressFromWebClient() {
        final String block = WebClient.builder().baseUrl(SdCommon.STRUCTURE.getRequestUrl())
                .codecs(item -> item.defaultCodecs()
                        .maxInMemorySize(20 * 1024 * 1024))
                .build()
                .get()
                .uri("/progress")
                .header(HttpHeaders.CONNECTION, "Close")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return JSONObject.parseObject(block);
    }

    private int calculatePercentage(JSONObject jsonObject) {
        final JSONObject state = jsonObject.getJSONObject("state");

        return (state.getInteger("sampling_step") * 100) / state.getInteger("sampling_steps");
    }

    private TaskResultVo handleTaskStatus(final TaskStructure taskStructure) {
        final Integer status = taskStructure.getStatus();
        TaskResultVo taskResultVo = new TaskResultVo().setPrompt(taskStructure.getPrompt());

        if (status.equals(DrawingStatusConstant.BUILDING)) {
            //等待列队
            HashMap<String, Object> map = new HashMap<>();
            map.put("progress", 0);
            taskResultVo.setStatus(DrawingStatusConstant.BUILDING).setExtra(map);

        } else if (status.equals(DrawingStatusConstant.BUILD_SUCCEED)) {
            //构建完毕
            taskResultVo.setStatus(DrawingStatusConstant.BUILD_SUCCEED)
                    //清空数据集 释放内存空间 剩余默认 30分钟自动删除
                    .setExtra(null).setImage(taskStructure.getImageUrl());
        } else {
            taskResultVo.setStatus(DrawingStatusConstant.BUILD_FAIL).setExtra(null);
        }

        return taskResultVo;
    }

}
