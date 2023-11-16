package com.cn.exception;


import lombok.Data;


/**
 * The type Login exception.
 */
@SuppressWarnings("all")
@Data
public class CloseException extends RuntimeException {

    private String message;

    private Integer code;


    public CloseException(final String message, final Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public CloseException() {

        this.message = message;
        this.code = 500;
    }

}
