package com.cn.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PayAlipayDto {

    @NotNull(message = "The product ID cannot be empty")
    private Long productCardId;
}
