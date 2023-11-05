package com.cn.exception;


import lombok.Data;


/**
 * The type Login exception.
 */
@SuppressWarnings("all")
@Data
public class LoginException extends RuntimeException {

    private String message;

    private Integer code;


    public LoginException(final String message, final Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public LoginException(final String message) {
        super(message);
        this.message = message;
        this.code = 500;
    }

}
