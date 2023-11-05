package com.cn.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EmailCaptchaDto {

    @Email(message = "注册邮箱格式不正确")
    @NotBlank(message = "注册邮箱不能为空")
    private String email;

}
