package com.cn.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class AlipayPayCodeVo implements Serializable {

    private String ordersId;

    private String productName;

    private LocalDateTime createdTime;

    private Double price;

    private String qrCode;



}
