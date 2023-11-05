package com.cn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddModelDto {

    @NotBlank(message = "The model name cannot be empty")
    private String modelName;

    @NotNull(message = "(whether it is a high level model) cannot be empty")
    private Boolean isSeniorModel;

    @NotNull(message = "the daily usage frequency cannot be empty")
    private Integer frequency;

}
