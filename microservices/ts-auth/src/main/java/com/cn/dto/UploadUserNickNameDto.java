package com.cn.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UploadUserNickNameDto {

    @NotBlank(message = "user nickname cannot be empty")
    private String nickName;

}
