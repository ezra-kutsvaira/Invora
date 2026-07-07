package dto.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest(

        @NotBlank(message = "Product name is required")
        String name,

        String description,

        @NotNull(message = "Unit price is required")
        @PositiveOrZero(message = "Unit price cannot be negative")
        BigDecimal unitPrice
) {
}
