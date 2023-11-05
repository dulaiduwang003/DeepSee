package com.cn.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@TableName(value = "ts_avatar")
@Accessors(chain = true)
public class TsAvatar {

    @TableId(type = IdType.AUTO)
    private Long avatarId;

    private String url;

    private String target;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
