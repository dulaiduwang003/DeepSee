package com.cn.service;

import com.cn.dto.GenerateDrawingDeleteDto;
import com.cn.vo.DrawingResultVo;
import com.cn.vo.PromptWordVo;
import com.cn.vo.TaskResultVo;

import java.util.List;

public interface DrawingService {

    /**
     * Gets drawing result list.
     *
     * @return the drawing result list
     */
    List<DrawingResultVo> gerDrawingOpus();


    /**
     * Gets drawing task.
     *
     * @param taskId the task id
     * @return the drawing task
     */
    TaskResultVo getDrawingTask(String taskId);


    /**
     * Gets random prompt word.
     *
     * @param type the type
     * @return the random prompt word
     */
    PromptWordVo getRandomPromptWord(String type);

    /**
     * Delete drawing opus by task id.
     *
     * @param dto the dto
     */
    void deleteDrawingOpusByTaskId(final GenerateDrawingDeleteDto dto);


}
