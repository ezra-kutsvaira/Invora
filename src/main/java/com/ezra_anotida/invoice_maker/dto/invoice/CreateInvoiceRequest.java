package com.ezra_anotida.invoice_maker.dto.invoice;

import com.ezra_anotida.invoice_maker.dto.invoiceitem.CreateInvoiceItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateInvoiceRequest(
        @NotNull(message = "Customer is required")
        Long customerId,

        @NotNull(message = "Invoice date is required")
        LocalDate invoiceDate,

        @NotNull(message = "Due date is required")
        LocalDate dueDate,

        @PositiveOrZero(message = "Discount cannot be negative")
        BigDecimal discountAmount,

        String notes,

        @Valid
        List<CreateInvoiceItemRequest> items
) {
}
