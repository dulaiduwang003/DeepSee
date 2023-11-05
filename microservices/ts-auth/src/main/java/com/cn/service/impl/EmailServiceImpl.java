package com.cn.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cn.constant.EmailConstant;
import com.cn.dto.EmailCodeDto;
import com.cn.entity.TsUser;
import com.cn.exception.AuthException;
import com.cn.exception.EmailException;
import com.cn.mapper.TsUserMapper;
import com.cn.service.EmailService;
import com.cn.utils.RedisUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;

@Service
@RequiredArgsConstructor
@SuppressWarnings("all")
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    private final TsUserMapper tsUserMapper;

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
            final TsUser user = tsUserMapper.selectOne(new QueryWrapper<TsUser>()
                    .lambda().eq(TsUser::getEmail, dto.getEmail())
                    .select(TsUser::getUserId));
            if (user != null) {
                throw new AuthException("该账号已经存在!");
            }

            tsUserMapper.insert(new TsUser()
                    .setEmail(dto.getEmail())
                    .setPassword(SaSecureUtil.md5BySalt(dto.getPassword(), SALT_PASSWORD)));
            redisUtils.delKey(KEY);
        } else {
            throw new EmailException("验证码错误!");
        }
    }

    @Override
    public void getEmailCode(final String email) {
        final String code = RandomStringUtils.random(6, true, true).toUpperCase();
        Context context = new Context();
        context.setVariable("code", code);
        String process = templateEngine.process("emailCode.html", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setSubject("TS GPT验证码");
            helper.setFrom(username);
            helper.setTo(email);
            helper.setSentDate(new Date());
            helper.setText(process, true);
            redisUtils.setValueTimeout(EmailConstant.CAPTCHA_CODE + email, code, 300);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("获取邮箱验证码失败 信息:{}, 位置", e.getMessage(), e.getClass());
            throw new EmailException("获取邮箱验证码失败!请稍后再试!", 500);
        }
    }


}
