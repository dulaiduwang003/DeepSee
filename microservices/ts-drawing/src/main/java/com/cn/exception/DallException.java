package com.cn.exception;

import lombok.Data;

@SuppressWarnings("all")
@Data
public class DallException extends RuntimeException {

    private String message;

    private Integer code;


    public DallException(final String message, final Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public DallException(final String message) {
        super(message);
        this.message = message;
        this.code = 500;
    }
}
