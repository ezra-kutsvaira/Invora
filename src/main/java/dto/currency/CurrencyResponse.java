package dto.currency;

import java.math.BigDecimal;

public record CurrencyResponse(
        Long id,

        String code,

        String name,

        String symbol,

        BigDecimal exchangeRateToBase,

        boolean active
) {
}
