package com.cn.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UseMicroTemplateDto {

    @NotNull(message = "模板标识不能为空")
    private Integer microAppId;

}
