package com.cn.exception;

import lombok.Data;

@SuppressWarnings("all")
@Data
public class DrawingException extends RuntimeException {

    private String message;

    private Integer code;


    public DrawingException(final String message, final Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public DrawingException(final String message) {
        super(message);
        this.message = message;
        this.code = 500;
    }
}
