package com.cn.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings("all")
public class DallModel {

    private String model = "dall-e-2";

    private String prompt;

    private String size;

    private String mask;

    private String image;

    private Integer n = 1;

}
