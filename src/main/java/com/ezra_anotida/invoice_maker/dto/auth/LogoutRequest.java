package com.ezra_anotida.invoice_maker.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(

        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {}
