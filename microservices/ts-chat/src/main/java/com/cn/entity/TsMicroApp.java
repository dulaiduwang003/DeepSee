package com.cn.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@TableName(value = "ts_micro_app")
@Accessors(chain = true)
public class TsMicroApp {

    @TableId(type = IdType.AUTO)
    private Long microAppId;

    private String icon;

    private String title;

    private String introduce;

    private String chineseIssue;

    private String englishIssue;

    private String chineseAnswer;

    private String englishAnswer;

    private Long microCategoryId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
