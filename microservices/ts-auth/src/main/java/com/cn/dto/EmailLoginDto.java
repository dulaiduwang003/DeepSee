package com.cn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * emailLoginDto
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class EmailLoginDto {

    @NotBlank(message = " mailbox cannot be empty")
    private String email;

    @Size(min = 1, max = 20, message = "login password is in the wrong format")
    private String password;
}
