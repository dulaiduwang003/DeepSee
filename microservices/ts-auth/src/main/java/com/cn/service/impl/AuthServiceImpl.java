package com.cn.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cn.constant.EmailConstant;
import com.cn.constant.UserCacheConstant;
import com.cn.dto.EmailCodeDto;
import com.cn.dto.EmailLoginDto;
import com.cn.entity.TsAvatar;
import com.cn.entity.TsUser;
import com.cn.exception.AuthException;
import com.cn.exception.EmailException;
import com.cn.exception.LoginException;
import com.cn.mapper.TsAvatarMapper;
import com.cn.mapper.TsUserMapper;
import com.cn.service.AuthService;
import com.cn.structure.UserInfoStructure;
import com.cn.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

/**
 * The type Auth service.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("all")
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    private final TsUserMapper tsUserMapper;

    private final TsAvatarMapper tsAvatarMapper;

    private final RedisUtils redisUtils;

    @Value(value = "${spring.mail.username}")
    private String username;

    private static final String SALT_PASSWORD = "TS";


    @Override
    public void emailEnroll(final EmailCodeDto dto) {
        final String code = dto.getCode();
        final String KEY = EmailConstant.CAPTCHA_CODE + dto.getEmail();
        final Object value = redisUtils.getValue(KEY);
        if (value != null && value.equals(code)) {
            final TsUser user = tsUserMapper.selectOne(new QueryWrapper<TsUser>().lambda().eq(TsUser::getEmail, dto.getEmail()).select(TsUser::getUserId));
            if (user != null) {
                throw new AuthException("该邮箱已被注册");
            }

            tsUserMapper.insert(new TsUser().setEmail(dto.getEmail()).setPassword(SaSecureUtil.md5BySalt(dto.getPassword(), SALT_PASSWORD)));
            redisUtils.delKey(KEY);
        } else {
            throw new EmailException("验证码不正确!");
        }
    }


    @Override
    public String emailAuthLogin(final EmailLoginDto dto) {
        final TsUser user = tsUserMapper.selectOne(new QueryWrapper<TsUser>().lambda().eq(TsUser::getEmail, dto.getEmail())
                //加盐
                .eq(TsUser::getPassword, SaSecureUtil.md5BySalt(dto.getPassword(), SALT_PASSWORD))
                .select(TsUser::getType, TsUser::getUserId, TsUser::getEmail, TsUser::getOpenId, TsUser::getAvatarId));
        if (user == null) {
            //no matching data
            throw new LoginException("账号或密码错误");
        }
        final TsAvatar tsAvatar = tsAvatarMapper.selectOne(
                new QueryWrapper<TsAvatar>()
                        .lambda()
                        .eq(TsAvatar::getAvatarId, user.getAvatarId())
                        .select(TsAvatar::getUrl)
        );
        //构建用户缓存数据
        final UserInfoStructure userInfoStructure = new UserInfoStructure()
                .setEmail(user.getEmail())
                .setType(user.getType())
                .setOpenId(user.getOpenId())
                .setNickName(user.getNickName());
        if (tsAvatar != null) {
            userInfoStructure.setAvatar(tsAvatar.getUrl());
        }
        StpUtil.login(user.getUserId());
        StpUtil.getSession()
                //设置用户数据缓存
                .set(UserCacheConstant.USER_INFO_DATA, userInfoStructure);
        return StpUtil.getTokenValue();

    }

    @Override
    public void emailForget(final EmailCodeDto dto) {
        final String code = dto.getCode();
        final String KEY = EmailConstant.CAPTCHA_CODE + dto.getEmail();
        final Object value = redisUtils.getValue(KEY);
        if (value != null && value.equals(code)) {
            final Long l = tsUserMapper.selectCount(new QueryWrapper<TsUser>().lambda().eq(TsUser::getEmail, dto.getEmail()).select(TsUser::getUserId));
            if (l <= 0) {
                throw new AuthException("该邮箱未注册");
            }
            tsUserMapper.update(new TsUser().setPassword(SaSecureUtil.md5BySalt(dto.getPassword(), SALT_PASSWORD)), new QueryWrapper<TsUser>().lambda().eq(TsUser::getEmail, dto.getEmail()));
            redisUtils.delKey(KEY);
        } else {
            throw new EmailException("邮箱验证码错误");
        }
    }

    @Override
    public String wechatAuthorizedLogin(final String code) {
        return null;
    }
}
