package dto.invoiceitem;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;


public record CreateInvoiceItemRequest(
        Long productId,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than zero")
        Integer quantity,

        @NotNull(message = "Unit price is required")
        @Positive(message = "Unit price must be greater than zero")
        BigDecimal unitPrice
) {
}
