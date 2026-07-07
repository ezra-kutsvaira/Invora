package dto.invoiceitem;

import java.math.BigDecimal;

public record InvoiceItemResponse(
        Long id,

        Long productId,

        String productName,

        String description,

        Integer quantity,

        BigDecimal unitPrice,

        BigDecimal lineTotal
) {
}
