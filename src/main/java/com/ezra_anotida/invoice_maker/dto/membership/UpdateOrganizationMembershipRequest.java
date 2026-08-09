package com.ezra_anotida.invoice_maker.dto.membership;

import com.ezra_anotida.invoice_maker.enums.MembershipStatus;
import com.ezra_anotida.invoice_maker.enums.OrganizationRole;

public record UpdateOrganizationMembershipRequest(
        OrganizationRole role,
        MembershipStatus status
) {
}
