package com.ezra_anotida.invoice_maker.dto.product;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record UpdateProductRequest(
        String name,
        String description,

        @PositiveOrZero(message = "Unit price cannot be negative")
        BigDecimal unitPrice,

        Boolean active,

        @PositiveOrZero(message = "Stock quantity cannot be negative")
        Integer stockQuantity
) {
}
