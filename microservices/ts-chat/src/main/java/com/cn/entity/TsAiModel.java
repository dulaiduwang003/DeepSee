package com.cn.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@TableName(value = "ts_ai_model")
@Accessors(chain = true)
public class TsAiModel {

    @TableId(type = IdType.AUTO)
    private Long aiModelId;

    private String modelName;

    private String modelValue;

    private LocalDateTime createdTime;

    private LocalDateTime updateTime;
}
