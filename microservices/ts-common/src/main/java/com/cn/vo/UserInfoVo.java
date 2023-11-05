package com.cn.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserInfoVo {

    private String nickName;

    private String avatar;

    private String type;

}
