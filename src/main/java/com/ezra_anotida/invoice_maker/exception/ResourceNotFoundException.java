package com.ezra_anotida.invoice_maker.exception;

public class ResourceNotFoundException extends InvoraException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(
                ApiErrorCode.RESOURCE_NOT_FOUND,
                "%s with %s '%s' was not found".formatted(resourceName, fieldName, fieldValue)
        );
    }

    public ResourceNotFoundException(String message) {
        super(ApiErrorCode.RESOURCE_NOT_FOUND, message);
    }
}
