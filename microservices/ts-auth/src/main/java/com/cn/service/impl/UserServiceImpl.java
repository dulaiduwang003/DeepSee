package com.cn.service.impl;

import com.cn.mapper.TsUserMapper;
import com.cn.service.UserService;
import com.cn.structure.UserInfoStructure;
import com.cn.utils.UserUtils;
import com.cn.vo.UserInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final TsUserMapper tsUserMapper;

    @Override
    public UserInfoVo getCurrentUserInfo() {
        //获取当前登录用户缓存数据(包含头像)
        final UserInfoStructure currentUserInfo = UserUtils.getCurrentUserInfo();
        return new UserInfoVo()
                .setNickName(currentUserInfo.getNickName())
                .setType(currentUserInfo.getType())
                .setAvatar(currentUserInfo.getAvatar());
    }
}
