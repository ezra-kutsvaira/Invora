package com.ezra_anotida.invoice_maker.mapper.exception;

public class DuplicateResourceException extends InvoraException {

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(
                ApiErrorCode.DUPLICATE_RESOURCE,
                "%s with %s '%s' already exists".formatted(resourceName, fieldName, fieldValue)
        );
    }

    public DuplicateResourceException(String message) {
        super(ApiErrorCode.DUPLICATE_RESOURCE, message);
    }
}
