package com.ezra_anotida.invoice_maker.dto.membership;

import com.ezra_anotida.invoice_maker.enums.MembershipStatus;
import com.ezra_anotida.invoice_maker.enums.OrganizationRole;

import java.time.LocalDateTime;

public record OrganizationMembershipResponse(
        Long id,
        Long organizationId,
        String organizationName,
        Long userId,
        String userName,
        String userEmail,
        OrganizationRole role,
        MembershipStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
