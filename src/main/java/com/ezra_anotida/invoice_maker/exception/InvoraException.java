package com.ezra_anotida.invoice_maker.exception;

import java.util.Objects;

public abstract class InvoraException extends RuntimeException {

    private final ApiErrorCode errorCode;

    protected InvoraException(ApiErrorCode errorCode, String message) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode, "Error code is required");
    }

    public ApiErrorCode getErrorCode() {
        return errorCode;
    }
}
