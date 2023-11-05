package com.cn.controller;

import com.cn.dto.EmailCodeDto;
import com.cn.dto.EmailLoginDto;
import com.cn.exception.AuthException;
import com.cn.exception.EmailException;
import com.cn.exception.LoginException;
import com.cn.msg.Result;
import com.cn.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 雨纷纷旧故里草木深
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * email account login
     */
    @PostMapping(value = "/email/login", name = "email login", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result emailLogin(@RequestBody @Validated EmailLoginDto dto) {
        try {
            return Result.data(authService.emailAuthLogin(dto));
        } catch (LoginException ex) {
            return Result.error(ex.getMessage());
        }
    }


    /**
     * sign up for an account
     */
    @PostMapping(value = "/email/enroll", name = "email enroll", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result emailEnroll(@RequestBody @Validated EmailCodeDto dto) {
        try {
            authService.emailEnroll(dto);
            return Result.ok();
        } catch (AuthException | EmailException ex) {
            return Result.error(ex.getMessage());
        }
    }


    /**
     * retrieve your password
     */
    @PostMapping(value = "/email/forgot", name = "email retrieve your password", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result emailForgot(@RequestBody @Validated EmailCodeDto dto) {
        try {
            authService.emailForget(dto);
            return Result.ok();
        } catch (AuthException | EmailException ex) {
            return Result.error(ex.getMessage());
        }
    }

}
