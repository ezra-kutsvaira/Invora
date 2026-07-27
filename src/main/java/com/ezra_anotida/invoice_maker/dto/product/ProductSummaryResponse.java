package com.ezra_anotida.invoice_maker.dto.product;

import java.math.BigDecimal;

public record ProductSummaryResponse(
        Long id,

        String name,

        BigDecimal unitPrice,

        Boolean active
) {
}
