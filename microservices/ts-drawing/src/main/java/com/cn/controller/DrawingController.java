package com.cn.controller;

import com.cn.dto.GenerateDrawingDeleteDto;
import com.cn.exception.DrawingException;
import com.cn.msg.Result;
import com.cn.service.DrawingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/drawing")
@RequiredArgsConstructor
public class DrawingController {

    private final DrawingService drawingService;


    /**
     * Delete drawing by task id result.
     *
     * @param dto the dto
     * @return the result
     */
    @PostMapping(value = "/delete/opus", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result deleteDrawingByTaskId(@RequestBody @Validated GenerateDrawingDeleteDto dto) {
        try {
            drawingService.deleteDrawingOpusByTaskId(dto);
            return Result.ok();
        } catch (DrawingException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * Gets all drawing.
     *
     * @return the all drawing
     */
    @GetMapping(value = "/get/opus", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getDrawingOpus() {
        return Result.data(drawingService.gerDrawingOpus());
    }


    /**
     * Gets task.
     *
     * @param taskId the task id
     * @return the task
     */
    @GetMapping(value = "/get/task/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getTask(@PathVariable String taskId) {
        return Result.data(drawingService.getDrawingTask(taskId));
    }


    /**
     * Gets prompt words randomly.
     *
     * @param type the type
     * @return the prompt words randomly
     */
    @GetMapping(value = "/get/prompt/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getPromptWordsRandomly(@PathVariable String type) {
        return Result.data(drawingService.getRandomPromptWord(type));
    }




}
