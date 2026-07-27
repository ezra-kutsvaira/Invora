package com.ezra_anotida.invoice_maker.dto.customer;

public record CustomerSummaryResponse(
        Long id,
        String customerName,
        String email,
        String phone,
        Boolean active
) {
}
