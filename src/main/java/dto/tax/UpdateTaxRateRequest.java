package dto.tax;

import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdateTaxRateRequest(
        String taxName,

        @PositiveOrZero(message = "Tax rate cannot be negative")
        BigDecimal rate,

        Boolean active
) {
}
