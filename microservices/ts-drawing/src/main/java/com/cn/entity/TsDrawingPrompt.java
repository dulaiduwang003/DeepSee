package com.cn.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@TableName(value = "ts_drawing_prompt")
@Accessors(chain = true)
public class TsDrawingPrompt {

    @TableId(type = IdType.AUTO)
    private Long tsDrawingPromptId;

    private String prompt;

    private String type;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
