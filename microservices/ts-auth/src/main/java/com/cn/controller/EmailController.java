package com.cn.controller;


import com.cn.dto.EmailCaptchaDto;
import com.cn.exception.EmailException;
import com.cn.msg.Result;
import com.cn.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;


    /**
     * get email verification code
     *
     * @param dto the dto
     * @return the email code
     */
    @PostMapping(value = "/send/code", name = "get email verification code", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result sendEmailCode(@RequestBody @Validated EmailCaptchaDto dto) {
        try {
            emailService.getEmailCode(dto.getEmail());
            return Result.ok();
        } catch (EmailException ex) {
            return Result.error(ex.getMessage());
        }
    }
}
