package com.ezra_anotida.invoice_maker.exception.security;

public class RefreshTokenReuseException  extends  RuntimeException{

    public RefreshTokenReuseException (String message){
        super(message);
    }

    public RefreshTokenReuseException(String message, Throwable cause){
        super(message, cause);
    }


}
