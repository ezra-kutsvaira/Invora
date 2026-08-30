package com.ezra_anotida.invoice_maker.controller;

import com.ezra_anotida.invoice_maker.dto.company.CompanyProfileResponse;
import com.ezra_anotida.invoice_maker.dto.company.CreateCompanyProfileRequest;
import com.ezra_anotida.invoice_maker.dto.company.UpdateCompanyProfileRequest;
import com.ezra_anotida.invoice_maker.service.CompanyProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations/{organizationId}/company-profile")
public class CompanyProfileController {

    private final CompanyProfileService companyProfileService;


    public CompanyProfileController(CompanyProfileService companyProfileService) {
        this.companyProfileService = companyProfileService;
    }

    @PostMapping
    public ResponseEntity<CompanyProfileResponse> createCompanyProfile (@PathVariable ("organizationId") Long organizationId, @Valid @RequestBody CreateCompanyProfileRequest createCompanyProfileRequest){

        CompanyProfileResponse companyProfile = companyProfileService.createCompanyProfile(organizationId, createCompanyProfileRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(companyProfile);
    }

    @GetMapping
    public ResponseEntity <CompanyProfileResponse>  getCompanyProfile (@PathVariable ("organizationId") Long organizationId){

        CompanyProfileResponse companyProfile = companyProfileService.getCompanyProfile(organizationId);

        return ResponseEntity.ok(companyProfile);
    }

    @PutMapping
    public ResponseEntity<CompanyProfileResponse> updateCompanyProfile (@PathVariable ("organizationId") Long organizationId, @Valid @RequestBody UpdateCompanyProfileRequest request){

        CompanyProfileResponse updatedCompanyProfile = companyProfileService.updateCompanyProfile(organizationId, request);

        return ResponseEntity.ok(updatedCompanyProfile);

    }
}
