package com.ezra_anotida.invoice_maker.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/invoices/42");
    }

    @Test
    void returnsNotFoundForMissingResource() {
        ResourceNotFoundException exception =
                new ResourceNotFoundException("Invoice", "id", 42L);

        ResponseEntity<ApiErrorResponse> response =
                handler.handleResourceNotFound(exception, request);

        assertErrorResponse(
                response,
                HttpStatus.NOT_FOUND,
                ApiErrorCode.RESOURCE_NOT_FOUND,
                "Invoice with id '42' was not found"
        );
    }

    @Test
    void returnsConflictForDuplicateResource() {
        DuplicateResourceException exception =
                new DuplicateResourceException("Customer", "email", "customer@example.com");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleDuplicateResource(exception, request);

        assertErrorResponse(
                response,
                HttpStatus.CONFLICT,
                ApiErrorCode.DUPLICATE_RESOURCE,
                "Customer with email 'customer@example.com' already exists"
        );
    }

    @Test
    void returnsBadRequestForInvalidInput() {
        InvalidRequestException exception =
                new InvalidRequestException("Invoice ID cannot be null");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleInvalidRequest(exception, request);

        assertErrorResponse(
                response,
                HttpStatus.BAD_REQUEST,
                ApiErrorCode.INVALID_REQUEST,
                "Invoice ID cannot be null"
        );
    }

    @Test
    void returnsConflictForInvalidResourceState() {
        InvalidResourceStateException exception =
                new InvalidResourceStateException("Only draft invoices can be issued");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleInvalidResourceState(exception, request);

        assertErrorResponse(
                response,
                HttpStatus.CONFLICT,
                ApiErrorCode.INVALID_RESOURCE_STATE,
                "Only draft invoices can be issued"
        );
    }

    @Test
    void returnsUnprocessableEntityForBusinessRuleViolation() {
        BusinessRuleException exception =
                new BusinessRuleException("Discount amount cannot exceed the invoice subtotal");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleBusinessRule(exception, request);

        assertErrorResponse(
                response,
                HttpStatus.UNPROCESSABLE_ENTITY,
                ApiErrorCode.BUSINESS_RULE_VIOLATION,
                "Discount amount cannot exceed the invoice subtotal"
        );
    }

    private void assertErrorResponse(
            ResponseEntity<ApiErrorResponse> response,
            HttpStatus expectedStatus,
            ApiErrorCode expectedErrorCode,
            String expectedMessage
    ) {
        ApiErrorResponse body = response.getBody();

        assertEquals(expectedStatus, response.getStatusCode());
        assertNotNull(body);
        assertEquals(expectedStatus.value(), body.status());
        assertEquals(expectedStatus.getReasonPhrase(), body.error());
        assertEquals(expectedErrorCode.name(), body.code());
        assertEquals(expectedMessage, body.message());
        assertEquals("/api/invoices/42", body.path());
    }
}
