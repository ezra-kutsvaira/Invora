package dto.payment;

import enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentResponse(
        Long id,

        Long invoiceDate,

        String invoiceNumber,

        LocalDate paymentDate,

        BigDecimal amount,

        PaymentMethod paymentMethod,

        String referenceNumber,

        String notes
) {
}
