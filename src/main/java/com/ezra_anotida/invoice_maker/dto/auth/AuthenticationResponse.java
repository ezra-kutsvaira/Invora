package com.ezra_anotida.invoice_maker.dto.auth;

public record AuthenticationResponse(

        String accessToken,

        String tokenType,

        long expiresIn,

        String refreshToken
) {}
