package com.cn.exception;


import lombok.Data;


/**
 * The type Login exception.
 */
@SuppressWarnings("all")
@Data
public class EmailException extends RuntimeException {

    private String message;

    private Integer code;


    public EmailException(final String message, final Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public EmailException(final String message) {
        super(message);
        this.message = message;
        this.code = 500;
    }

}
