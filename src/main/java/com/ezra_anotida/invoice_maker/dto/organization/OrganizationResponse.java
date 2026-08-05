package com.ezra_anotida.invoice_maker.dto.organization;

import com.ezra_anotida.invoice_maker.enums.OrganizationStatus;

import java.time.LocalDateTime;

public record OrganizationResponse(
        Long id,

        String name,

        String slug,

        OrganizationStatus status,

        Boolean active,

        Boolean companyProfileConfigured,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}
