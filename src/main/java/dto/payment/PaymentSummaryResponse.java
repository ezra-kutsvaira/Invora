package dto.payment;

import enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentSummaryResponse(
        Long id,

        String invoiceNumber,

        String customerName,

        LocalDate paymentDate,

        BigDecimal amount,

        PaymentMethod paymentMethod,

        String referenceNumber
) {
}


