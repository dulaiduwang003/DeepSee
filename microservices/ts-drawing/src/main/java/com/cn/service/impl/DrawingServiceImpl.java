package com.cn.service.impl;

import com.cn.common.SdCommon;
import com.cn.common.structure.SdStructure;
import com.cn.common.structure.TaskStructure;
import com.cn.constant.DrawingConstant;
import com.cn.constant.DrawingStatusConstant;
import com.cn.dto.DallTaskDto;
import com.cn.dto.SdTaskDto;
import com.cn.enums.TaskEnum;
import com.cn.model.SdModel;
import com.cn.service.DrawingService;
import com.cn.utils.ImageUtils;
import com.cn.utils.RedisUtils;
import com.cn.utils.UserUtils;
import com.cn.vo.SdParamVo;
import com.cn.vo.TaskResultVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DrawingServiceImpl implements DrawingService {

    private final RedisUtils redisUtils;

    private final RedisTemplate<String, Object> redisTemplate;


    private String checkForDuplicateTasks() {
        final Long currentLoginId = UserUtils.getCurrentLoginId();
        //生成任务ID
        final String taskId = DrawingConstant.DRAWING_TASK + currentLoginId;
        //这里要判断是否存在上一次任务 如果存在 并且上一个任务已经被完成 这需要判断任务类型 根据任务类型执行 删除资源操作
        if (redisUtils.doesItExist(taskId)) {
            final TaskStructure value = (TaskStructure) redisUtils.getValue(taskId);
            if (value.getStatus().equals(DrawingStatusConstant.BUILDING)) {
                throw new RuntimeException("If a task is already in progress, please do not submit it repeatedly");
            }
        }
        //设置任务队列
        redisUtils.setValue(taskId, new TaskStructure()
                //设置任务状态为 构件中
                .setStatus(DrawingStatusConstant.BUILDING)
        );
        return taskId;
    }

    @Override
    public SdParamVo getSdParam() {
        final SdStructure config = SdCommon.getConfig();

        return new SdParamVo()
                .setSamplerList(config.getSamplerList())
                .setStepsList(config.getStepsList())
                .setModelList(config.getModelList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDallDrawingTask(final DallTaskDto dto) {
        //检查任务队列以及 设置任务队列
        final String taskId = checkForDuplicateTasks();
        redisTemplate.opsForList().leftPush(DrawingConstant.DRAWING_TASK_QUEUE,
                new TaskStructure()
                        //设置任务ID
                        .setTaskId(taskId)
                        .setPrompt(dto.getPrompt())
                        //指向DALL任务
                        .setTaskEnum(TaskEnum.DALL));
    }

    @Override
    public void addSdDrawingTask(final SdTaskDto dto) {
        //检查任务队列以及 设置任务队列
        final String taskId = checkForDuplicateTasks();
        //判断是否上传图片
        final SdModel sdModel = new SdModel()
                .setPrompt(dto.getPrompt())
                .setSteps(dto.getSteps())
                .setWidth(dto.getWidth())
                .setHeight(dto.getHeight())
                .setOverride_settings(
                        new SdModel.Override()
                                //设置动态模型昵称
                                .setSd_model_checkpoint(dto.getModelName())
                )
                .setSampler_index(dto.getSampler_index())
                .setNegative_prompt(dto.getNegative_prompt());
        if (dto.getImages() != null) {
            //转为BASE64
            sdModel.setInit_images(List.of(ImageUtils.convertToBase64(dto.getImages())));
        }
        redisTemplate.opsForList().leftPush(DrawingConstant.DRAWING_TASK_QUEUE,
                new TaskStructure()
                        //设置任务ID
                        .setTaskId(taskId)
                        .setPrompt(dto.getPrompt())
                        //指向SD任务
                        .setTaskEnum(TaskEnum.SD)
                        .setExtra(sdModel)
        );
    }

    @Override
    public TaskResultVo getDrawingTask() {
        final String taskId = DrawingConstant.DRAWING_TASK + UserUtils.getCurrentLoginId();
        if (redisUtils.doesItExist(taskId)) {
            final TaskStructure value = (TaskStructure) redisUtils.getValue(taskId);

            if (value.getStatus().equals(DrawingStatusConstant.BUILD_SUCCEED)) {
                //删除KEY
                redisUtils.delKey(taskId);
                final TaskResultVo taskResultVo = new TaskResultVo()
                        .setPrompt(value.getPrompt())
                        .setStatus(value.getStatus())
                        .setImage(value.getImageUrl());
                if (value.getExtra() != null) {
                    taskResultVo.setResponse(value.getExtra().toString());
                }
                return taskResultVo;
            } else if (value.getStatus().equals(DrawingStatusConstant.BUILD_FAIL)) {
                //删除KEY
                redisUtils.delKey(taskId);
                return new TaskResultVo()
                        .setPrompt(value.getPrompt())
                        .setStatus(value.getStatus());
            }
        }
        return null;
    }

}
