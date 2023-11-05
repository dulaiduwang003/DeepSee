package com.cn.service;


import com.cn.dto.EmailCodeDto;
import com.cn.dto.EmailLoginDto;

/**
 * auth interface
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
public interface AuthService {


    /**
     * Email auth login .
     *
     * @param dto the dto
     * @return the string
     */
    String emailAuthLogin(final EmailLoginDto dto);

    /**
     * Email enroll.
     *
     * @param dto the dto
     */
    void emailEnroll(final EmailCodeDto dto);


    /**
     * Email forget.
     */
    void emailForget(final EmailCodeDto dto);

    /**
     * WeChat authorized login
     *
     * @param code the code
     * @return the string
     */
    String wechatAuthorizedLogin(final String code);
}
