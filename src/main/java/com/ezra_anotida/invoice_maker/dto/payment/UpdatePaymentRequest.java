package com.ezra_anotida.invoice_maker.dto.payment;

import com.ezra_anotida.invoice_maker.enums.PaymentMethod;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdatePaymentRequest(
        LocalDate paymentDate,

        @Positive(message = "Payment amount must be greater than zero")
        BigDecimal amount,

        PaymentMethod paymentMethod,

        String referenceNumber,

        String notes
) {
}