package com.ezra_anotida.invoice_maker.dto.payment;


import com.ezra_anotida.invoice_maker.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecordPaymentRequest(
        @NotNull(message = "Invoice is required")
        Long invoiceId,

        LocalDate paymentDate,

        @NotNull(message = "Amount is required")
        @Positive(message = "Payment amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        String referenceNumber,
        String notes
) {
}