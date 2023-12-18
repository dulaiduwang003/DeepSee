package com.cn.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@TableName(value = "ts_micro_category")
@Accessors(chain = true)
public class TsMicroCategory {

    @TableId(type = IdType.AUTO)
    private Long microCategoryId;

    private String elIcon;

    private String categoryName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
