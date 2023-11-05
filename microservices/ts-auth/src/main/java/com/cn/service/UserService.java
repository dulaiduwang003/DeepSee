package com.cn.service;

import com.cn.vo.UserInfoVo;

public interface UserService {


    /**
     * Gets current user info.
     *
     * @return the current user info
     */
    UserInfoVo getCurrentUserInfo();
}
