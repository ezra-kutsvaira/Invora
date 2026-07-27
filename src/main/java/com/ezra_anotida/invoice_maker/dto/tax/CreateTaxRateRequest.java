package com.ezra_anotida.invoice_maker.dto.tax;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateTaxRateRequest(
        @NotBlank(message = "Tax name is required")
        String taxName,

        @NotNull(message = "tax rate is required")
        @PositiveOrZero(message = "Tax rate cannot be negative")
        BigDecimal taxRate
) {
}
