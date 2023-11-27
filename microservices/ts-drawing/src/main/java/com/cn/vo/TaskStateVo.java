package com.cn.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class TaskStateVo implements Serializable {

    private String imageBase64;

    private String expectedBeCompleted;

}
