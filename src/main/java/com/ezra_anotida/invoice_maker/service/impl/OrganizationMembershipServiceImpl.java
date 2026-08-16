package com.ezra_anotida.invoice_maker.service.impl;

import com.ezra_anotida.invoice_maker.dto.membership.CreateOrganizationMembershipRequest;
import com.ezra_anotida.invoice_maker.dto.membership.OrganizationMembershipResponse;
import com.ezra_anotida.invoice_maker.dto.membership.UpdateOrganizationMembershipRequest;
import com.ezra_anotida.invoice_maker.entity.Organization;
import com.ezra_anotida.invoice_maker.entity.OrganizationMembership;
import com.ezra_anotida.invoice_maker.entity.User;
import com.ezra_anotida.invoice_maker.enums.MembershipStatus;
import com.ezra_anotida.invoice_maker.enums.OrganizationRole;
import com.ezra_anotida.invoice_maker.enums.OrganizationStatus;
import com.ezra_anotida.invoice_maker.mapper.exception.DuplicateResourceException;
import com.ezra_anotida.invoice_maker.mapper.exception.InvalidRequestException;
import com.ezra_anotida.invoice_maker.mapper.exception.InvalidResourceStateException;
import com.ezra_anotida.invoice_maker.mapper.exception.ResourceNotFoundException;
import com.ezra_anotida.invoice_maker.repository.OrganizationMembershipRepository;
import com.ezra_anotida.invoice_maker.repository.OrganizationRepository;
import com.ezra_anotida.invoice_maker.repository.UserRepository;
import com.ezra_anotida.invoice_maker.service.OrganizationMembershipService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrganizationMembershipServiceImpl implements OrganizationMembershipService {

    private final OrganizationMembershipRepository membershipRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public OrganizationMembershipServiceImpl(OrganizationMembershipRepository membershipRepository, OrganizationRepository organizationRepository, UserRepository userRepository) {
        this.membershipRepository = membershipRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public OrganizationMembershipResponse addMember(Long organizationId, CreateOrganizationMembershipRequest request) {

        validateId(organizationId, "Organization");

        if (request == null){
            throw new InvalidRequestException("Membership request is required");
        }

        Organization organization = findActiveOrganization(organizationId);

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.userId()));

        if (!user.isEnabled()) {
            throw new InvalidResourceStateException("A disabled user cannot join an organization");
        }

        if (membershipRepository.existsByOrganizationIdAndUserId(organizationId, user.getId())) {
            throw new DuplicateResourceException("Organization membership", "organizationId/userId", organizationId + "/" + user.getId()
            );
        }

        OrganizationMembership membership = new OrganizationMembership();
        membership.setOrganization(organization);
        membership.setUser(user);
        membership.setRole(request.role());
        membership.setStatus(MembershipStatus.ACTIVE);

        return toResponse(membershipRepository.save(membership));
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationMembershipResponse getMember(Long organizationId, Long membershipId) {

        return toResponse(findMembership(organizationId, membershipId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrganizationMembershipResponse> getMembers(Long organizationId, Pageable pageable) {

        findActiveOrganization(organizationId);

        if (pageable == null) {
            throw new InvalidRequestException("Pageable is required");
        }

        return membershipRepository.findByOrganizationId(organizationId, pageable).map(this::toResponse);
    }

    @Override
    public OrganizationMembershipResponse updateMember(Long organizationId, Long membershipId, UpdateOrganizationMembershipRequest request) {

        if (request == null) throw new InvalidRequestException("Membership update request is required");

        OrganizationMembership membership = findMembership(organizationId, membershipId);
        OrganizationRole newRole = request.role();
        MembershipStatus newStatus = request.status();

        if (newRole == null && newStatus == null) {
            throw new InvalidRequestException("At least one membership field must be provided");
        }

        if (newRole != null) membership.setRole(newRole);
        if (newStatus != null) membership.setStatus(newStatus);

        return toResponse(membershipRepository.save(membership));
    }

    @Override
    public void removeMember(Long organizationId, Long membershipId) {

        OrganizationMembership membership = findMembership(organizationId, membershipId);

        membership.setStatus(MembershipStatus.SUSPENDED);

        membershipRepository.save(membership);
    }

    private Organization findActiveOrganization(Long organizationId) {

        validateId(organizationId, "Organization");

        return organizationRepository.findByIdAndStatus(organizationId, OrganizationStatus.ACTIVE)

                .orElseThrow(() -> new ResourceNotFoundException("Active organization", "id", organizationId));
    }

    private OrganizationMembership findMembership(Long organizationId, Long membershipId) {

        findActiveOrganization(organizationId);

        validateId(membershipId, "Membership");

        return membershipRepository.findByIdAndOrganizationId(membershipId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organization membership",
                        "id",
                        membershipId
                ));
    }

    private void validateId(Long id, String resource) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException(resource + " id must be greater than zero");
        }
    }

    private OrganizationMembershipResponse toResponse(OrganizationMembership membership) {
        return new OrganizationMembershipResponse(
                membership.getId(),
                membership.getOrganization().getId(),
                membership.getOrganization().getName(),
                membership.getUser().getId(),
                membership.getUser().getName(),
                membership.getUser().getEmail(),
                membership.getRole(),
                membership.getStatus(),
                membership.getCreatedAt(),
                membership.getUpdatedAt()
        );
    }
}
