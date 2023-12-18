package com.cn.controller;

import com.cn.dto.SdTaskDto;
import com.cn.exception.DrawingException;
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


    /**
     * Gets sd param.
     *
     * @return the sd param
     */
    @GetMapping(value = "/get/param", name = "get the list of SD drawing parameters", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getSdParam() {
        return Result.data(sdService.getSdParam());
    }





    /**
     * Push task result.
     *
     * @param dto the dto
     * @return the result
     */
    @PostMapping(value = "/push/generate", name = "publish an sd drawing task", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result pushGenerateTask(@RequestBody @Validated SdTaskDto dto) {
        try {
            return Result.data(sdService.addSdDrawingTask(dto));
        } catch (DrawingException e) {
            return Result.error(e.getMessage());
        }
    }



}
