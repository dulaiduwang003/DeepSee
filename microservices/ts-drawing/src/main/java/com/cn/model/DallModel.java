package com.cn.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class DallModel implements Serializable {

    private String model = "dall-e-3";

    private String prompt;

//    private String size = "512x512";

//    private String quality = "standard";
//
//    private Integer n = 1;
}
