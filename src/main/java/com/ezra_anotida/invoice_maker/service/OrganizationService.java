package com.ezra_anotida.invoice_maker.service;

import com.ezra_anotida.invoice_maker.dto.organization.CreateOrganizationRequest;
import com.ezra_anotida.invoice_maker.dto.organization.OrganizationResponse;
import com.ezra_anotida.invoice_maker.dto.organization.UpdateOrganizationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrganizationService {

    OrganizationResponse createOrganization(CreateOrganizationRequest request);

    OrganizationResponse getOrganizationById(Long organizationId);

    OrganizationResponse getOrganizationBySlug(String slug);

    Page<OrganizationResponse> getAllOrganizations(Pageable pageable);

    Page<OrganizationResponse> getActiveOrganizations(Pageable pageable);

    Page<OrganizationResponse> searchOrganizations(String keyword, Pageable pageable);

    OrganizationResponse updateOrganization(Long organizationId, UpdateOrganizationRequest request);

    void deactivateOrganization(Long organizationId);

    OrganizationResponse reactivateOrganization(Long organizationId);

    void suspendOrganization(Long organizationId);
}