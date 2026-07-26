package com.ezra_anotida.invoice_maker.exception;

public class InvalidResourceStateException extends BusinessRuleException {

    public InvalidResourceStateException(String message) {
        super(ApiErrorCode.INVALID_RESOURCE_STATE, message);
    }
}
