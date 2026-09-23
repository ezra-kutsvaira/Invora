package com.ezra_anotida.invoice_maker.security.token;

import java.time.Instant;

public record IssuedRefreshToken(
        String value,
        Instant expiresAt
) {
}
