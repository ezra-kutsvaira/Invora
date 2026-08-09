package com.ezra_anotida.invoice_maker.service;

import com.ezra_anotida.invoice_maker.dto.membership.CreateOrganizationMembershipRequest;
import com.ezra_anotida.invoice_maker.dto.membership.OrganizationMembershipResponse;
import com.ezra_anotida.invoice_maker.dto.membership.UpdateOrganizationMembershipRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrganizationMembershipService {

    OrganizationMembershipResponse addMember(
            Long organizationId,
            CreateOrganizationMembershipRequest request
    );

    OrganizationMembershipResponse getMember(Long organizationId, Long membershipId);

    Page<OrganizationMembershipResponse> getMembers(Long organizationId, Pageable pageable);

    OrganizationMembershipResponse updateMember(
            Long organizationId,
            Long membershipId,
            UpdateOrganizationMembershipRequest request
    );

    void removeMember(Long organizationId, Long membershipId);
}
