package com.ezra_anotida.invoice_maker.mapper.exception;

public class InvalidRequestException extends InvoraException {

    public InvalidRequestException(String message) {
        super(ApiErrorCode.INVALID_REQUEST, message);
    }
}
