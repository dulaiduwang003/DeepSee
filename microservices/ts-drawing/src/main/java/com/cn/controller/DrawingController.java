package com.cn.controller;

import com.cn.dto.DallTaskDto;
import com.cn.dto.SdTaskDto;
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

    @GetMapping(value = "/get/sd/param", name = "获取SD绘制参数列表", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getSdParam() {
        return Result.data(drawingService.getSdParam());
    }


    @PostMapping(value = "/push/dall/task", name = "发布Dall绘图任务", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result pushDallTask(@RequestBody @Validated DallTaskDto dto) {
        try {
            drawingService.addDallDrawingTask(dto);
            return Result.ok();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping(value = "/push/sd/task", name = "发布SD绘图任务", consumes = "multipart/form-data")
    public Result pushDallTask(SdTaskDto dto) {
        System.out.println(dto);
        try {
            drawingService.addSdDrawingTask(dto);
            return Result.ok();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


    @GetMapping(value = "/get/task", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getDallTask() {
        return Result.data(drawingService.getDrawingTask());
    }


}
