package com.cn.controller;


import com.cn.msg.Result;
import com.cn.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    /**
     * Gets current user info.
     *
     * @return the current user info
     */
    @GetMapping(value = "/get/userinfo", name = "获取当前登陆用户信息", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getCurrentUserInfo() {
        return Result.data(userService.getCurrentUserInfo());
    }
}
