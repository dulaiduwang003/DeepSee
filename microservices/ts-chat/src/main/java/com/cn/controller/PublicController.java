package com.cn.controller;

import com.cn.msg.Result;
import com.cn.service.MicroAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

    private final MicroAppService microAppService;

    /**
     * Gets micro applications list.
     *
     * @return the micro applications list
     */
    @GetMapping(value = "/get/micro/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getMicroApplicationsPage(@RequestParam(defaultValue = "1") final int pageNum) {
        return Result.data(microAppService.getMicroAppPage(pageNum));
    }


    /**
     * Search micro app page result.
     *
     * @param pageNum the page num
     * @param prompt  the prompt
     * @return the result
     */
    @GetMapping(value = "/search/micro/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result searchMicroAppPage(@RequestParam(defaultValue = "1") final int pageNum, @RequestParam String prompt) {
        return Result.data(microAppService.searchMicroApp(prompt, pageNum));
    }
}
