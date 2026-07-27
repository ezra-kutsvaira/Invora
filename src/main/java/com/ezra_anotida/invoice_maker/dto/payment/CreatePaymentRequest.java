package com.ezra_anotida.invoice_maker.dto.payment;

import com.ezra_anotida.invoice_maker.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePaymentRequest(

        @NotNull(message =  "Payment amount is required")
        @DecimalMin (value = "0.01" , message = "Payment amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @NotNull(message = "Payment is required")
        LocalDate paymentDate,

        @Size(max = 100, message = "Reference number cannot exceed 100 characters")
        String referenceNumber,

        @Size(max = 500, message = "Payment notes cannot exceed 500 characters")
        String notes
) {}
