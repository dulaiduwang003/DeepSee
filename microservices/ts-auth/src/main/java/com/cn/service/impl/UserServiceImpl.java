package com.cn.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cn.constant.UserCacheConstant;
import com.cn.entity.TsUser;
import com.cn.enums.FileEnum;
import com.cn.mapper.TsUserMapper;
import com.cn.service.UserService;
import com.cn.structure.UserInfoStructure;
import com.cn.utils.StringUtils;
import com.cn.utils.UploadUtil;
import com.cn.utils.UserUtils;
import com.cn.vo.UserInfoVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final TsUserMapper tsUserMapper;

    private final UploadUtil uploadUtil;

    @Override
    public UserInfoVo getCurrentUserInfo() {
        //获取当前登录用户缓存数据(包含头像)
        final UserInfoStructure currentUserInfo = UserUtils.getCurrentUserInfo();
        return new UserInfoVo()
                .setNickName(currentUserInfo.getNickName())
                .setType(currentUserInfo.getType())
                .setAvatarUrl(currentUserInfo.getAvatarUrl());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadAvatar(final MultipartFile file) {
        final String newAvatar = uploadUtil.uploadFile(file, FileEnum.AVATAR.getDec());
        System.out.println(newAvatar);
        //当前用户ID
        final Long currentLoginId = UserUtils.getCurrentLoginId();
        //获取用户真实头像地址
        final TsUser tsUser = tsUserMapper.selectOne(new QueryWrapper<TsUser>()
                .lambda()
                .eq(TsUser::getUserId, currentLoginId)
                .select(TsUser::getAvatarUrl)
        );
        final String oldAvatar = tsUser.getAvatarUrl();
        //修改头像
        tsUserMapper
                .updateById(
                        tsUser
                                .setAvatarUrl(newAvatar)
                                .setUserId(currentLoginId)
                );
        //获取原有的用户数据信息 仅更新一次头像即可
        final UserInfoStructure currentUserInfo = UserUtils.getCurrentUserInfo();
        //更新头像
        currentUserInfo.setAvatarUrl(newAvatar);
        StpUtil.getSession()
                //设置用户数据缓存
                .set(UserCacheConstant.USER_INFO_DATA, currentUserInfo);
        if (!StringUtils.isEmpty(oldAvatar)) {
            uploadUtil.deletedFile(oldAvatar);
        }

    }
}
