package com.cn.structure;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class AlipayCacheStructure {

    private String url;

    private LocalDateTime createdTime;

    private String ordersId;

    private Double price;

    private String productName;

}
