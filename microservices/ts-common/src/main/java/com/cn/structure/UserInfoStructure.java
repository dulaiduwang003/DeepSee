package com.cn.structure;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * The type User info structure.
 */
@Data
@Accessors(chain = true)
public class UserInfoStructure implements Serializable {

    private Long userId;

    private String nickName;

    private String openId;

    private String email;

    private String avatar;

    private String type;

    private LocalDateTime expirationTime;

}
