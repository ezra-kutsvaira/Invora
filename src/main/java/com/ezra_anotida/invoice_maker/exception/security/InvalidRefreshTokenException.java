package com.ezra_anotida.invoice_maker.exception.security;

public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException(String message){
        super(message);
    }

    public InvalidRefreshTokenException(String message, Throwable cause){
        super(message, cause);
    }
}
