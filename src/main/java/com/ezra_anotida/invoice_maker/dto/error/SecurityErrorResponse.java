package com.ezra_anotida.invoice_maker.dto.error;

import java.time.Instant;

public record SecurityErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path

) {}
