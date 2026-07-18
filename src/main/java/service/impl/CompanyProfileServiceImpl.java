package service.impl;

import dto.company.CompanyProfileResponse;
import dto.company.CreateCompanyProfileRequest;
import dto.company.UpdateCompanyProfileRequest;
import entity.CompanyProfile;
import jakarta.persistence.EntityNotFoundException;
import mapper.CompanyProfileMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.CompanyProfileRepository;
import service.CompanyProfileService;

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
                .orElseThrow(() -> new EntityNotFoundException("Active company profile not found"));

    }

    private void validateCompanyProfileDoesNotExist(){
        if(companyProfileRepository.findByActiveTrue().isPresent()){
            throw new IllegalArgumentException("Active Company profile already exists");
        }
    }
}
