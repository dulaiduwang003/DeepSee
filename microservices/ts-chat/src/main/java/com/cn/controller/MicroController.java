package com.cn.controller;

import com.cn.dto.UseMicroTemplateDto;
import com.cn.msg.Result;
import com.cn.service.MicroAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/micro")
@RequiredArgsConstructor
public class MicroController {

    /**
     * The Micro app service.
     */
    private final MicroAppService microAppService;

    /**
     * Gets micro applications list.
     *
     * @return the micro applications list
     */
    @PostMapping(value = "/use/template", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result useMicroTemplate(@RequestBody @Validated UseMicroTemplateDto dto) {
        microAppService.useMicroApp(dto);
        return Result.ok();
    }

}
