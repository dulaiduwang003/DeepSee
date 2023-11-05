package com.cn.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.cn.constant.UserCacheConstant;
import com.cn.structure.UserInfoStructure;


/**
 * 用户工具类
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
public class UserUtils {


    public static Long getLoginIdByToken(final String token) {
        final Object loginIdObject = StpUtil.getLoginIdByToken(token);
        return Long.parseLong(String.valueOf(loginIdObject));
    }

    public static Long getCurrentLoginId() {
        return Long.parseLong(String.valueOf(StpUtil.getLoginId()));
    }

    public static UserInfoStructure getCurrentUserInfo() {
        final Object o = StpUtil.getSession().get(UserCacheConstant.USER_INFO_DATA);
        return (UserInfoStructure) o;
    }


}
