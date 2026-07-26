package com.ezra_anotida.invoice_maker.exception;

public class BusinessRuleException extends InvoraException {

    public BusinessRuleException(String message) {
        super(ApiErrorCode.BUSINESS_RULE_VIOLATION, message);
    }

    protected BusinessRuleException(ApiErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
