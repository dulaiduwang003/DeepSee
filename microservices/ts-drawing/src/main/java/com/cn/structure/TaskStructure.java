package com.cn.structure;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class TaskStructure implements Serializable {

    private String drawingType;

    private String taskId;

    private String prompt;

    private String imageUrl;

    private String status;

    private Object extra;

}
