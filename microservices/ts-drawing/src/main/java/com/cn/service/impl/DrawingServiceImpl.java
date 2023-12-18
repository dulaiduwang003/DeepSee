package com.cn.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cn.common.SdCommon;
import com.cn.constant.DrawingConstant;
import com.cn.constant.DrawingStatusConstant;
import com.cn.dto.GenerateDrawingDeleteDto;
import com.cn.entity.TsDrawingPrompt;
import com.cn.entity.TsGenerateDrawing;
import com.cn.enums.DrawingTypeEnum;
import com.cn.exception.DrawingException;
import com.cn.mapper.TsDrawingPromptMapper;
import com.cn.mapper.TsGenerateDrawingMapper;
import com.cn.service.DrawingService;
import com.cn.structure.TaskStructure;
import com.cn.utils.RedisUtils;
import com.cn.utils.UploadUtil;
import com.cn.utils.UserUtils;
import com.cn.vo.DrawingResultVo;
import com.cn.vo.PromptWordVo;
import com.cn.vo.TaskResultVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * The type Drawing service.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DrawingServiceImpl implements DrawingService {

    private final TsGenerateDrawingMapper tsGenerateDrawingMapper;

    private final TsDrawingPromptMapper tsDrawingPromptMapper;

    private final RedisUtils redisUtils;

    private final UploadUtil uploadUtil;


    @Override
    public List<DrawingResultVo> gerDrawingOpus() {
        return tsGenerateDrawingMapper.selectList(new QueryWrapper<TsGenerateDrawing>().lambda().eq(TsGenerateDrawing::getUserId, UserUtils.getCurrentLoginId()).select(TsGenerateDrawing::getPrompt, TsGenerateDrawing::getType, TsGenerateDrawing::getGenerateDrawingId, TsGenerateDrawing::getCreatedTime, TsGenerateDrawing::getStatus, TsGenerateDrawing::getUrl).orderByAsc(TsGenerateDrawing::getCreatedTime)).stream().map(t -> new DrawingResultVo().setStatus(t.getStatus()).setTaskId(t.getGenerateDrawingId()).setDrawingType(t.getType()).setPrompt(t.getPrompt()).setImage(t.getUrl()).setCreatedTime(t.getCreatedTime())).toList();
    }

    @Override
    public void deleteDrawingOpusByTaskId(GenerateDrawingDeleteDto dto) {

        final TsGenerateDrawing tsGenerateDrawing = tsGenerateDrawingMapper.selectOne(new QueryWrapper<TsGenerateDrawing>()
                .lambda()
                .eq(TsGenerateDrawing::getGenerateDrawingId, dto.getTaskId())
                .select(TsGenerateDrawing::getUrl, TsGenerateDrawing::getStatus)
        );
        if (tsGenerateDrawing != null) {
            final String status = tsGenerateDrawing.getStatus();
            if (Objects.equals(status, DrawingStatusConstant.PENDING) || Objects.equals(status, DrawingStatusConstant.PROCESSING)) {
                throw new DrawingException("该任务正在等待被系统处理,请等待处理完毕后再删除");
            }
            //删除文件
            if (!status.equals(DrawingStatusConstant.DISUSE)) {
                uploadUtil.deletedFile(tsGenerateDrawing.getUrl());
            }

            tsGenerateDrawingMapper.delete(new QueryWrapper<TsGenerateDrawing>().lambda().eq(TsGenerateDrawing::getUserId, UserUtils.getCurrentLoginId()).eq(TsGenerateDrawing::getGenerateDrawingId, dto.getTaskId()));
        } else
            throw new DrawingException("任务不存在或已被系统自动删除");


    }

    @Override
    public PromptWordVo getRandomPromptWord(final String type) {

        final TsDrawingPrompt tsDrawingPrompt = tsDrawingPromptMapper.selectOne(new QueryWrapper<TsDrawingPrompt>()
                .eq("type", type)
                .select("prompt")
                .orderByAsc("RAND()")
                .last("LIMIT 1")
        );
        if (tsDrawingPrompt != null) {
            return new PromptWordVo().setPrompt(tsDrawingPrompt.getPrompt());
        }
        return null;

    }

    @Override
    public TaskResultVo getDrawingTask(final String taskId) {
        String assetTaskId = DrawingConstant.TASK + taskId;
        if (redisUtils.doesItExist(assetTaskId)) {
            final TaskStructure taskStructure = (TaskStructure) redisUtils.getValue(assetTaskId);
            //判断任务来源
            if (taskStructure.getDrawingType().equals(DrawingTypeEnum.SD.getDec())) {
                return handleGetSdTask(taskStructure, taskId);
            } else
                return handleGetDallTask(taskStructure, taskId);

        }
        //任务已被抛弃 或不存在
        return null;
    }


    private TaskResultVo handleGetDallTask(final TaskStructure taskStructure, final String taskId) {
        //判断SD是否存在于链表
        final String handleTaskId = String.valueOf(redisUtils.hashGet(DrawingConstant.DALL_EXECUTION, taskId));
        if (taskId.equals(handleTaskId)) {
            return new TaskResultVo().setPrompt(taskStructure.getPrompt())
                    //预览效果
                    .setStatus(DrawingStatusConstant.PROCESSING);
        } else {
            return handleTaskStatus(taskStructure);
        }


    }


    private TaskResultVo handleGetSdTask(TaskStructure taskStructure, String taskId) {
        //判断当前处理的是否为 当前task
        final String handleTaskId = String.valueOf(redisUtils.getValue(DrawingConstant.SD_EXECUTION));
        //验证状态
        if (taskId.equals(handleTaskId)) {
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
                        .setStatus(DrawingStatusConstant.PROCESSING).setExtra(map);
            } catch (Exception e) {
                return new TaskResultVo().setPrompt(taskStructure.getPrompt()).setStatus(DrawingStatusConstant.PROCESSING);
            }

        } else {
            return handleTaskStatus(taskStructure);
        }
    }

    private JSONObject getProgressFromWebClient() {
        final String block = WebClient.builder().baseUrl(SdCommon.STRUCTURE.getRequestUrl()).codecs(item -> item.defaultCodecs().maxInMemorySize(20 * 1024 * 1024)).build().get().uri("/progress").header(HttpHeaders.CONNECTION, "Close").retrieve().bodyToMono(String.class).block();
        return JSONObject.parseObject(block);
    }

    private int calculatePercentage(JSONObject jsonObject) {
        final JSONObject state = jsonObject.getJSONObject("state");

        return (state.getInteger("sampling_step") * 100) / state.getInteger("sampling_steps");
    }

    private TaskResultVo handleTaskStatus(final TaskStructure taskStructure) {
        final String status = taskStructure.getStatus();
        TaskResultVo taskResultVo = new TaskResultVo().setPrompt(taskStructure.getPrompt());
        if (status.equals(DrawingStatusConstant.PENDING)) {
            taskResultVo.setStatus(DrawingStatusConstant.PENDING);
        } else if (status.equals(DrawingStatusConstant.SUCCEED)) {
            //构建完毕
            taskResultVo.setStatus(DrawingStatusConstant.SUCCEED)
                    //清空数据集 释放内存空间 剩余默认 30分钟自动删除
                    .setExtra(null).setImage(taskStructure.getImageUrl());
        } else {
            taskResultVo.setStatus(DrawingStatusConstant.DISUSE).setExtra(null);
        }

        return taskResultVo;
    }
}
