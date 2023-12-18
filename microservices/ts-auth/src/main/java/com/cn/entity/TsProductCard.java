package com.cn.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@TableName(value = "ts_product_card")
@Accessors(chain = true)
public class TsProductCard {

    @TableId(type = IdType.AUTO)
    private Long productCardId;

    private String productName;

    private Long days;

    private Double price;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
