package com.ezra_anotida.invoice_maker.controller;

import com.ezra_anotida.invoice_maker.dto.organization.CreateOrganizationRequest;
import com.ezra_anotida.invoice_maker.dto.organization.OrganizationResponse;
import com.ezra_anotida.invoice_maker.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    public ResponseEntity<OrganizationResponse> createOrganization(@Valid @RequestBody CreateOrganizationRequest createOrganizationRequest) {

        OrganizationResponse response = organizationService.createOrganization(createOrganizationRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{organizationId}")
    public ResponseEntity<OrganizationResponse> getOrganization(@PathVariable Long id) {

        OrganizationResponse organizationResponse = organizationService.getOrganizationById(id);

        return ResponseEntity.ok(organizationResponse);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<OrganizationResponse> getOrganizationBySlug(@PathVariable String slug) {

        OrganizationResponse organizationResponse = organizationService.getOrganizationBySlug(slug);

        return ResponseEntity.ok(organizationResponse);
    }

    @GetMapping
    public ResponseEntity<Page<OrganizationResponse>> getAllOrganizations(Pageable pageable) {

        Page<OrganizationResponse> organizationResponse = organizationService.getAllOrganizations(pageable);

        return ResponseEntity.ok(organizationResponse);
    }

    @GetMapping("/active")
    public ResponseEntity<Page<OrganizationResponse>> getAllActiveOrganizations(Pageable pageable){

        Page<OrganizationResponse> organizationResponse = organizationService.getActiveOrganizations(pageable);

        return ResponseEntity.ok(organizationResponse);
    }

    @PutMapping("/{organizationId}")
    public ResponseEntity<Void> deactivateOrganization (@PathVariable Long organizationId){

        OrganizationResponse organizationResponse = organizationService.deactivateOrganization(organizationId);

    }

}
