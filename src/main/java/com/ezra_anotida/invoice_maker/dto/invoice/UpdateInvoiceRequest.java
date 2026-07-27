package com.ezra_anotida.invoice_maker.dto.invoice;

import com.ezra_anotida.invoice_maker.dto.invoiceitem.UpdateInvoiceItemRequest;
import com.ezra_anotida.invoice_maker.enums.InvoiceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record UpdateInvoiceRequest(
        Long customerId,
        LocalDate invoiceDate,
        LocalDate dueDate,

        @PositiveOrZero(message = "Discount cannot be negative")
        BigDecimal discountAmount,

        InvoiceStatus status,
        String notes,

        @Valid
        List<UpdateInvoiceItemRequest> items
) {
}
