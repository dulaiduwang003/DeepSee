package com.cn.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class ProductCardVo implements Serializable {

    private Long productCardId;

    private String productName;

    private Double price;

    private Long days;

    private Boolean isSelected = false;
}
