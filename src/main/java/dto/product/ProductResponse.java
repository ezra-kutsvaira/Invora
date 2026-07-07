package dto.product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,

        String name,

        String description,

        BigDecimal unitPrice,

        boolean active
) {
}
