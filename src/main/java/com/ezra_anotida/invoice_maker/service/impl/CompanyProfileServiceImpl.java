package com.ezra_anotida.invoice_maker.service.impl;

import com.ezra_anotida.invoice_maker.entity.Organization;
import com.ezra_anotida.invoice_maker.exception.DuplicateResourceException;
import com.ezra_anotida.invoice_maker.exception.InvalidRequestException;
import com.ezra_anotida.invoice_maker.exception.ResourceNotFoundException;
import com.ezra_anotida.invoice_maker.dto.company.CompanyProfileResponse;
import com.ezra_anotida.invoice_maker.dto.company.CreateCompanyProfileRequest;
import com.ezra_anotida.invoice_maker.dto.company.UpdateCompanyProfileRequest;
import com.ezra_anotida.invoice_maker.entity.CompanyProfile;
import com.ezra_anotida.invoice_maker.mapper.CompanyProfileMapper;
import com.ezra_anotida.invoice_maker.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ezra_anotida.invoice_maker.repository.CompanyProfileRepository;
import com.ezra_anotida.invoice_maker.service.CompanyProfileService;

@Service
@Transactional
public class CompanyProfileServiceImpl implements CompanyProfileService {

    private final CompanyProfileRepository companyProfileRepository;
    private final CompanyProfileMapper companyProfileMapper;
    private final OrganizationRepository organizationRepository;


    public CompanyProfileServiceImpl(CompanyProfileRepository companyProfileRepository, CompanyProfileMapper companyProfileMapper, OrganizationRepository organizationRepository) {
        this.companyProfileRepository = companyProfileRepository;
        this.companyProfileMapper = companyProfileMapper;
        this.organizationRepository = organizationRepository;
    }


    @Override
    public CompanyProfileResponse createCompanyProfile(Long organizationId, CreateCompanyProfileRequest request) {

        validateCompanyProfileDoesNotExist(organizationId);

        Organization organization = findOrganizationById(organizationId);

        CompanyProfile companyProfile = companyProfileMapper.toEntity(request);

        companyProfile.setOrganization(organization);

        companyProfile.setActive(true);

        CompanyProfile savedCompanyProfile = companyProfileRepository.save(companyProfile);

        return companyProfileMapper.toResponse(savedCompanyProfile);
    }


    @Override
    @Transactional(readOnly = true)
    public CompanyProfileResponse getCompanyProfile(Long organizationId) {

        CompanyProfile companyProfile = findActiveCompanyProfile(organizationId);


        return companyProfileMapper.toResponse(companyProfile);
    }

    @Override
    public CompanyProfileResponse updateCompanyProfile(Long organizationId, UpdateCompanyProfileRequest request) {

        CompanyProfile companyProfile = findActiveCompanyProfile(organizationId);

        companyProfileMapper.updateEntityFromRequest(request, companyProfile);

        CompanyProfile updateCompanyProfile = companyProfileRepository.save(companyProfile);

        return companyProfileMapper.toResponse(updateCompanyProfile);
    }

    private CompanyProfile findActiveCompanyProfile(Long organizationId) {
        validateOrganizationId(organizationId);

        return companyProfileRepository.
                findByOrganizationIdAndActiveTrue(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Active company profile was not found"));
    }


    private void validateCompanyProfileDoesNotExist(Long organizationId) {

        validateOrganizationId(organizationId);

        boolean profileExists = companyProfileRepository.existsByOrganizationIdAndActiveTrue(organizationId);

        if(profileExists){
            throw new DuplicateResourceException("An active company profile already exists " + "for this organization ");
        }
    }

    public Organization findOrganizationById (Long organizationId){

        validateOrganizationId(organizationId);

        return organizationRepository
                .findById(organizationId).orElseThrow(() -> new ResourceNotFoundException("Organization", "id", organizationId));
    }

    private void validateOrganizationId(Long organizationId) {
        if(organizationId == null || organizationId <= 0){
            throw new InvalidRequestException("Organization must be greater than zero");
        }
    }
}
