package com.cn.exceptions;

import lombok.Data;

@SuppressWarnings("all")
@Data
public class WechatException extends RuntimeException {

    private String message;

    private Integer code;


    public WechatException(final String message, final Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public WechatException(final String message) {
        super(message);
        this.message = message;
        this.code = 500;
    }
}
