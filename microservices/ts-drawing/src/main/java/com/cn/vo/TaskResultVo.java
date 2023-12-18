package com.cn.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class TaskResultVo implements Serializable {

    private String status;

    private String image;

    private String prompt;

    private Object extra;


}
