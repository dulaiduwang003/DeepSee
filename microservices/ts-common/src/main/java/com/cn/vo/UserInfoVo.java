package com.cn.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class UserInfoVo {

    private String nickName;

    private String avatar;

    private String type;

    private Member member;

    @Data
    @Accessors(chain = true)
    public static class Member {

        private Boolean isMember;

        private LocalDateTime expirationTime;

    }

}
