package com.ezra_anotida.invoice_maker.dto.payment;

import com.ezra_anotida.invoice_maker.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentResponse(
        Long id,

        Long invoiceId,

        String invoiceNumber,

        LocalDate paymentDate,

        BigDecimal amount,

        PaymentMethod paymentMethod,

        String referenceNumber,

        String notes
) {
}
