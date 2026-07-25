package dto.currency;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateCurrencyRequest(
        @NotBlank(message = "Currency code is required")
        String code,

        @NotBlank(message = "Currency name is required")
        String name,

        @NotBlank(message = "Currency symbol is required")
        String symbol,

        @NotNull(message = "Exchange rate is required")
        @Positive(message = "Exchange rate must be greater than zero")
        BigDecimal exchangeRateToBase

) {
}
