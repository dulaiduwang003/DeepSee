package com.cn.service;

import com.cn.dto.EmailCodeDto;

public interface EmailService {

    /**
     * Email enroll.
     *
     * @param dto the dto
     */
    void emailEnroll(final EmailCodeDto dto);

    /**
     * Gets email code.
     *
     * @param email the email
     */
    void getEmailCode(final String email);


}
