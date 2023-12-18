package com.cn.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@TableName(value = "ts_generate_drawing")
@Accessors(chain = true)
public class TsGenerateDrawing {

    @TableId(type = IdType.INPUT)
    private String generateDrawingId;

    private String prompt;

    private Long userId;

    private String status;

    private String url;

    private String type;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
