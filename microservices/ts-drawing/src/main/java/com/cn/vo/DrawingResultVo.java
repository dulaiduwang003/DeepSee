package com.cn.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class DrawingResultVo implements Serializable {

    private String taskId;

    private String image;

    private String status;

    private String prompt;

    private String drawingType;

    private LocalDateTime createdTime;
}
