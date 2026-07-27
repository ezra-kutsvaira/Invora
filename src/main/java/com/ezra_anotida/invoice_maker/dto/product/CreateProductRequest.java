package com.ezra_anotida.invoice_maker.dto.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest(

        @NotBlank(message = "Product name is required")
        String name,

        String description,

        @NotNull(message = "Unit price is required")
        @PositiveOrZero(message = "Unit price cannot be negative")
        BigDecimal unitPrice,

        @PositiveOrZero(message = "Stock quantity cannot be negative ")
        Integer stockQuantity
) {
}
