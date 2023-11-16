package com.cn.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * ts_user table
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
@Data
@TableName(value = "ts_user")
@Accessors(chain = true)
public class TsUser {

    @TableId(type = IdType.AUTO)
    private Long userId;

    private String openId;

    private String email;

    private String type;

    private String password;

    private String avatarUrl;


    private Integer frequency;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
