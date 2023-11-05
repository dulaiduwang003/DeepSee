package com.cn.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 雨纷纷旧故里草木深
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class EmailCodeDto {


    @Email(message = "mailbox is malformed")
    private String email;

    @Size(min = 5, max = 20, message = " password is between 5 20 digits")
    private String password;

    @NotBlank(message = "verification code cannot be empty")
    private String code;
}
