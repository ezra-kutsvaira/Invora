package service;

import dto.company.CompanyProfileResponse;
import dto.company.CreateCompanyProfileRequest;
import dto.company.UpdateCompanyProfileRequest;

public interface CompanyProfileService {
    CompanyProfileResponse createCompanyProfile(CreateCompanyProfileRequest request);

    CompanyProfileResponse getCompanyProfile();

    CompanyProfileResponse updateCompanyProfile(UpdateCompanyProfileRequest request);
}
