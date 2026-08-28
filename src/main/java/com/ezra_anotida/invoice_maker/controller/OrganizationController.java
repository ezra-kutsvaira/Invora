package com.ezra_anotida.invoice_maker.controller;

import com.ezra_anotida.invoice_maker.dto.organization.CreateOrganizationRequest;
import com.ezra_anotida.invoice_maker.dto.organization.OrganizationResponse;
import com.ezra_anotida.invoice_maker.dto.organization.UpdateOrganizationRequest;
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
    public ResponseEntity<OrganizationResponse> getOrganization(@PathVariable Long organizationId) {

        OrganizationResponse organizationResponse = organizationService.getOrganizationById(organizationId);

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

    @GetMapping("/search")
    public ResponseEntity<Page<OrganizationResponse>> searchOrganizations(@RequestParam String keyword, Pageable pageable){

         Page<OrganizationResponse> organizationResponse = organizationService.searchOrganizations(keyword, pageable);

         return ResponseEntity.ok(organizationResponse);
    }

    @PutMapping("/organizationId")
    public ResponseEntity<OrganizationResponse> updateOrganization(@PathVariable Long organizationId, @Valid @RequestBody UpdateOrganizationRequest updateOrganizationRequest){

        OrganizationResponse organizationResponse = organizationService.updateOrganization(organizationId, updateOrganizationRequest);

        return ResponseEntity.ok(organizationResponse);
    }

    @PatchMapping("/organizationId")
    public ResponseEntity<Void> deactivateOrganization(@PathVariable Long organizationId){

        organizationService.deactivateOrganization(organizationId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{organizationId}/reactivate")
    public ResponseEntity<OrganizationResponse> reactivateOrganization(@PathVariable Long organizationId){

        OrganizationResponse organizationResponse = organizationService.reactivateOrganization(organizationId);

        return ResponseEntity.ok(organizationResponse);
    }

    @PatchMapping("/{organizationId}/suspend")
    public ResponseEntity<Void> suspendOrganization(@PathVariable Long organizationId){

        organizationService.suspendOrganization(organizationId);

        return ResponseEntity.noContent().build();
    }


}
