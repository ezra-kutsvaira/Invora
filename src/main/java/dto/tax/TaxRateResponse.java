package dto.tax;

import java.math.BigDecimal;

public record TaxRateResponse(
        Long id,

        String taxName,

        BigDecimal rate,

        Boolean active
) {
}
