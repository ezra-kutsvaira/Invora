package com.ezra_anotida.invoice_maker.controller;

import com.ezra_anotida.invoice_maker.dto.membership.CreateOrganizationMembershipRequest;
import com.ezra_anotida.invoice_maker.dto.membership.OrganizationMembershipResponse;
import com.ezra_anotida.invoice_maker.dto.membership.UpdateOrganizationMembershipRequest;
import com.ezra_anotida.invoice_maker.service.OrganizationMembershipService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations/{organizationId}/memberships")
public class OrganizationMembershipController {

    private final OrganizationMembershipService membershipService;

    public OrganizationMembershipController(OrganizationMembershipService membershipService) {
        this.membershipService = membershipService;
    }

    // Add a member to an organization
    @PostMapping
    public ResponseEntity<OrganizationMembershipResponse> addMember(@PathVariable("organizationId") Long organizationId, @Valid @RequestBody CreateOrganizationMembershipRequest request) {

        OrganizationMembershipResponse membership = membershipService.addMember(organizationId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(membership);
    }

    // Get a particular membership
    @GetMapping("/{membershipId}")
    public ResponseEntity<OrganizationMembershipResponse> getMember(@PathVariable("organizationId") Long organizationId, @PathVariable("membershipId") Long membershipId) {

        OrganizationMembershipResponse membership = membershipService.getMember(organizationId, membershipId);

        return ResponseEntity.ok(membership);
    }

    // Get all organization memberships with pagination
    @GetMapping
    public ResponseEntity<Page<OrganizationMembershipResponse>> getMembers(@PathVariable("organizationId") Long organizationId, Pageable pageable) {

        Page<OrganizationMembershipResponse> memberships = membershipService.getMembers(organizationId, pageable);

        return ResponseEntity.ok(memberships);
    }

    // Update a membership
    @PutMapping("/{membershipId}")
    public ResponseEntity<OrganizationMembershipResponse> updateMember(@PathVariable("organizationId") Long organizationId, @PathVariable("membershipId") Long membershipId, @Valid @RequestBody UpdateOrganizationMembershipRequest request) {

        OrganizationMembershipResponse membership = membershipService.updateMember(organizationId, membershipId, request);

        return ResponseEntity.ok(membership);
    }

    // Remove a member from the organization
    @DeleteMapping("/{membershipId}")
    public ResponseEntity<Void> removeMember(@PathVariable("organizationId") Long organizationId, @PathVariable("membershipId") Long membershipId) {

        membershipService.removeMember(organizationId, membershipId);

        return ResponseEntity.noContent().build();
    }
}