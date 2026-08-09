package com.ezra_anotida.invoice_maker.dto.membership;

import com.ezra_anotida.invoice_maker.enums.OrganizationRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrganizationMembershipRequest(
        @NotNull @Positive Long userId,
        @NotNull OrganizationRole role
) {
}
