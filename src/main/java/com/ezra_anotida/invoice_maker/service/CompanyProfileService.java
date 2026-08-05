package com.ezra_anotida.invoice_maker.service;

import com.ezra_anotida.invoice_maker.dto.company.CompanyProfileResponse;
import com.ezra_anotida.invoice_maker.dto.company.CreateCompanyProfileRequest;
import com.ezra_anotida.invoice_maker.dto.company.UpdateCompanyProfileRequest;

public interface CompanyProfileService {
    CompanyProfileResponse createCompanyProfile(Long organizationId, CreateCompanyProfileRequest request);

    CompanyProfileResponse getCompanyProfile(Long organizationId);

    CompanyProfileResponse updateCompanyProfile(Long organizationId, UpdateCompanyProfileRequest request);
}
