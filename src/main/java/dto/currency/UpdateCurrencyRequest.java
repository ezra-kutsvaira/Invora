package dto.currency;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateCurrencyRequest(
        String code,

        String name,

        String symbol,

        @Positive(message = "Exchange rate must be greater than zero")
        BigDecimal exchangeRateToBase,

        Boolean active
) {
}
