package com.cn.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Accessors(chain = true)
@Getter
@Setter
public class ModelVo implements Serializable {

    private Integer modelIndex;

    private String modelName;

}
