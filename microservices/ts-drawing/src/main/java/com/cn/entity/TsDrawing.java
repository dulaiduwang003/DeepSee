package com.cn.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@TableName(value = "ts_drawing")
@Accessors(chain = true)
public class TsDrawing {

    @TableId(type = IdType.AUTO)
    private Long drawingId;

    private String url;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
