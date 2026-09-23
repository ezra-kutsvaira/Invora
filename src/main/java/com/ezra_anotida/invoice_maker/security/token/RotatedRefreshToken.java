package com.ezra_anotida.invoice_maker.security.token;

public record RotatedRefreshToken(
        Long userId,
        IssuedRefreshToken issuedRefreshToken
) {
}
