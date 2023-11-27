package com.cn.controller;

import com.cn.dto.SdTaskDto;
import com.cn.msg.Result;
import com.cn.service.SdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/sd")
@RequiredArgsConstructor
public class SdController {

    private final SdService sdService;


    @GetMapping(value = "/get/param", name = "get the list of SD drawing parameters", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getSdParam() {
        return Result.data(sdService.getSdParam());
    }


    @GetMapping(value = "/get/task/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getTask(@PathVariable String taskId) {
        return Result.data(sdService.getDrawingTask(taskId));
    }


    @PostMapping(value = "/push/task", name = "publish an sd drawing task", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result pushTask(@RequestBody @Validated SdTaskDto dto) {
        try {
            return Result.data(sdService.addSdDrawingTask(dto));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


}
