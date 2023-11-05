package com.cn.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信授权登录
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
@Data
public class WeChatAuthLoginDto {

    @NotBlank(message = "wechat authorization code cannot be empty")
    private String code;
}
