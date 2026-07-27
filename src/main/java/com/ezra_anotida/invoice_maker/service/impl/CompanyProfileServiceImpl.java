package com.ezra_anotida.invoice_maker.service.impl;

import com.ezra_anotida.invoice_maker.exception.DuplicateResourceException;
import com.ezra_anotida.invoice_maker.exception.ResourceNotFoundException;
import com.ezra_anotida.invoice_maker.dto.company.CompanyProfileResponse;
import com.ezra_anotida.invoice_maker.dto.company.CreateCompanyProfileRequest;
import com.ezra_anotida.invoice_maker.dto.company.UpdateCompanyProfileRequest;
import com.ezra_anotida.invoice_maker.entity.CompanyProfile;
import com.ezra_anotida.invoice_maker.mapper.CompanyProfileMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ezra_anotida.invoice_maker.repository.CompanyProfileRepository;
import com.ezra_anotida.invoice_maker.service.CompanyProfileService;

@Service
@Transactional
public class CompanyProfileServiceImpl implements CompanyProfileService {

    private final CompanyProfileRepository companyProfileRepository;
    private final CompanyProfileMapper companyProfileMapper;


    public CompanyProfileServiceImpl(CompanyProfileRepository companyProfileRepository, CompanyProfileMapper companyProfileMapper) {
        this.companyProfileRepository = companyProfileRepository;
        this.companyProfileMapper = companyProfileMapper;
    }

    //Creating company profile
    @Override
    public CompanyProfileResponse createCompanyProfile(CreateCompanyProfileRequest request) {

       validateCompanyProfileDoesNotExist();

        CompanyProfile companyProfile = companyProfileMapper.toEntity(request);

        //Activate it
        companyProfile.setActive(true);

        CompanyProfile savedCompanyProfile = companyProfileRepository.save(companyProfile);
        return  companyProfileMapper.toResponse(savedCompanyProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyProfileResponse getCompanyProfile() {

        CompanyProfile companyProfile = findActiveCompanyProfile();

        return companyProfileMapper.toResponse(companyProfile);
    }

    @Override
    public CompanyProfileResponse updateCompanyProfile(UpdateCompanyProfileRequest request) {

        CompanyProfile existingCompanyProfile = findActiveCompanyProfile();

        companyProfileMapper.updateEntityFromRequest(request, existingCompanyProfile);

        CompanyProfile updatedCompanyProfile = companyProfileRepository.save(existingCompanyProfile);

        return companyProfileMapper.toResponse(updatedCompanyProfile);
    }

    //Helper Methods

    private CompanyProfile findActiveCompanyProfile() {
        return companyProfileRepository.findByActiveTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Active company profile was not found"));

    }

    private void validateCompanyProfileDoesNotExist(){
        if(companyProfileRepository.findByActiveTrue().isPresent()){
            throw new DuplicateResourceException("An active company profile already exists");
        }
    }
}
