package com.cn.common.structure;

import com.cn.enums.TaskEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class TaskStructure implements Serializable {

    private TaskEnum taskEnum;

    private String taskId;

    private String prompt;

    private String imageUrl;

    private Integer status;

    private Object extra;

}
