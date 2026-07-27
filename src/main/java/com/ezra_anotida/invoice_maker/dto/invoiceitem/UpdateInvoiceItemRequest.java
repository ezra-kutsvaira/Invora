package com.ezra_anotida.invoice_maker.dto.invoiceitem;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateInvoiceItemRequest(
        Long productId,

        String description,

        @Positive(message = "Quantity must be greater than zero")
        Integer quantity,

        @Positive(message = "Unit price must be greater than zero")
        BigDecimal unitPrice
) {
}
