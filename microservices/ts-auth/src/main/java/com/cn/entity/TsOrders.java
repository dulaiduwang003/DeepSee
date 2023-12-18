package com.cn.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@TableName(value = "ts_orders")
@Accessors(chain = true)
public class TsOrders {

  @TableId(type = IdType.INPUT)
  private String ordersId;

  private Long userId;

  private String productName;

  private Double price;

  private long currency;

  private String status;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createdTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}
