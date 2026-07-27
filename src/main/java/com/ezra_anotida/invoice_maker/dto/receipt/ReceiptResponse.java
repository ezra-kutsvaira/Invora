package com.ezra_anotida.invoice_maker.dto.receipt;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReceiptResponse(
        Long id,

        String receiptNumber,

        LocalDate receiptDate,

        BigDecimal amount,

        Long paymentId,

        Long invoiceId,

        String invoiceNumber,

        String customerName
) {
}
