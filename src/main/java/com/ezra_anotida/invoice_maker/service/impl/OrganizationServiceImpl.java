package com.ezra_anotida.invoice_maker.service.impl;

import com.ezra_anotida.invoice_maker.dto.organization.CreateOrganizationRequest;
import com.ezra_anotida.invoice_maker.dto.organization.OrganizationResponse;
import com.ezra_anotida.invoice_maker.dto.organization.UpdateOrganizationRequest;
import com.ezra_anotida.invoice_maker.entity.Organization;
import com.ezra_anotida.invoice_maker.enums.OrganizationStatus;
import com.ezra_anotida.invoice_maker.mapper.exception.DuplicateResourceException;
import com.ezra_anotida.invoice_maker.mapper.exception.InvalidRequestException;
import com.ezra_anotida.invoice_maker.mapper.exception.InvalidResourceStateException;
import com.ezra_anotida.invoice_maker.mapper.exception.ResourceNotFoundException;
import com.ezra_anotida.invoice_maker.mapper.OrganizationMapper;
import com.ezra_anotida.invoice_maker.repository.OrganizationRepository;
import com.ezra_anotida.invoice_maker.service.OrganizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Locale;

@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private static final int MAX_SLUG_LENGTH = 100;

    private final OrganizationRepository organizationRepository;

    private final OrganizationMapper organizationMapper;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository, OrganizationMapper organizationMapper) {
        this.organizationRepository = organizationRepository;
        this.organizationMapper = organizationMapper;
    }

    @Override
    public OrganizationResponse createOrganization(CreateOrganizationRequest request) {

        validateCreateRequest(request);

        String normalizedName = normalizeRequiredName(request.name());

        String normalizedSlug = determineSlug(request.slug(), normalizedName);

        validateUniqueSlug(normalizedSlug, null);

        Organization organization = organizationMapper.toEntity(request);

        organization.setName(normalizedName);

        organization.setSlug(normalizedSlug);

        organization.setStatus(OrganizationStatus.ACTIVE);


        Organization savedOrganization = organizationRepository.save(organization);

        return organizationMapper.toResponse(savedOrganization);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationResponse getOrganizationById(Long organizationId) {

        Organization organization = findOrganizationById(organizationId);

        return organizationMapper.toResponse(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationResponse getOrganizationBySlug(String slug) {

        String normalizedSlug = normalizeRequiredSlug(slug);

        Organization organization =
                organizationRepository
                        .findBySlugIgnoreCase(normalizedSlug)
                        .orElseThrow(() -> new ResourceNotFoundException("Organization", "slug", normalizedSlug));

        return organizationMapper.toResponse(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrganizationResponse> getAllOrganizations(Pageable pageable) {
        validatePageable(pageable);

        return organizationRepository
                .findAll(pageable)
                .map(organizationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrganizationResponse> getActiveOrganizations(Pageable pageable) {

        validatePageable(pageable);

        return organizationRepository
                .findByStatus(OrganizationStatus.ACTIVE, pageable)
                .map(organizationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrganizationResponse> searchOrganizations(String keyword, Pageable pageable) {

        validatePageable(pageable);

        if (keyword == null || keyword.isBlank()) {
            throw new InvalidRequestException("Search keyword cannot be blank");
        }

        return organizationRepository
                .findByNameContainingIgnoreCase(
                        keyword.trim(),
                        pageable
                )
                .map(organizationMapper::toResponse);
    }

    @Override
    public OrganizationResponse updateOrganization(Long organizationId, UpdateOrganizationRequest request) {

        if (request == null) {
            throw new InvalidRequestException("Update organization request cannot be null");
        }

        Organization organization = findOrganizationById(organizationId);

        if (organization.getStatus() == OrganizationStatus.SUSPENDED) {
            throw new InvalidResourceStateException("A suspended organization cannot be updated");
        }

        String normalizedName = null;
        String normalizedSlug = null;

        if (request.name() != null) {
            normalizedName = normalizeRequiredName(request.name());
        }

        if (request.slug() != null) {
            normalizedSlug = normalizeRequiredSlug(request.slug());

            validateUniqueSlug(normalizedSlug, organization);
        }

        organizationMapper.updateEntityFromRequest(request, organization);

        if (normalizedName != null) {
            organization.setName(normalizedName);
        }

        if (normalizedSlug != null) {
            organization.setSlug(normalizedSlug);
        }

        Organization updatedOrganization =
                organizationRepository.save(organization);

        return organizationMapper.toResponse(
                updatedOrganization
        );
    }

    @Override
    public void deactivateOrganization(Long organizationId) {

        Organization organization = findOrganizationById(organizationId);

        if(organization.getStatus() == OrganizationStatus.INACTIVE){
            throw new InvalidResourceStateException("Organization is already inactive");
        }

        if(organization.getStatus() == OrganizationStatus.SUSPENDED){
            throw new InvalidResourceStateException("A suspended organization cannot be deactived");
        }

        organization.setStatus(OrganizationStatus.INACTIVE);

        organizationRepository.save(organization);
    }

    @Override
    public OrganizationResponse reactivateOrganization(Long organizationId) {

        Organization organization = findOrganizationById(organizationId);

        if (organization.getStatus() == OrganizationStatus.SUSPENDED) {
            throw new InvalidResourceStateException("A suspended organization cannot be reactivated");
        }

        if(organization.getStatus() == OrganizationStatus.ACTIVE){
            throw  new InvalidResourceStateException("Organization is already active");
        }

        organization.setStatus(OrganizationStatus.ACTIVE);

        Organization reactivatedOrganization = organizationRepository.save(organization);

        return organizationMapper.toResponse(reactivatedOrganization);
    }

    @Override
    public void suspendOrganization(Long organizationId) {

        Organization organization = findOrganizationById(organizationId);

        if (organization.getStatus() == OrganizationStatus.SUSPENDED) {
            throw new InvalidResourceStateException("Organization is already suspended");
        }

        organization.setStatus(OrganizationStatus.SUSPENDED);

        organizationRepository.save(organization);
    }

    private Organization findOrganizationById(Long organizationId) {

        validateOrganizationId(organizationId);

        return organizationRepository
                .findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", organizationId));
    }

    private void validateUniqueSlug(String slug, Organization existingOrganization) {

        boolean slugBelongsToCurrentOrganization = existingOrganization != null && existingOrganization.getSlug() != null && existingOrganization
                        .getSlug()
                        .equalsIgnoreCase(slug);

        if (!slugBelongsToCurrentOrganization && organizationRepository.existsBySlugIgnoreCase(slug)) {

            throw new DuplicateResourceException("Organization", "slug", slug);
        }
    }

    private String determineSlug(String requestedSlug, String organizationName) {
        if (requestedSlug == null || requestedSlug.isBlank()) {
            return generateSlug(organizationName);
        }

        return normalizeRequiredSlug(requestedSlug);
    }

    private String generateSlug(String organizationName) {

        String slug = Normalizer.normalize(organizationName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");

        if (slug.isBlank()) {
            throw new InvalidRequestException("A valid slug could not be generated " + "from the organization name");
        }

        if (slug.length() > MAX_SLUG_LENGTH) {
            slug = slug.substring(0, MAX_SLUG_LENGTH);

            slug = slug.replaceAll("-+$", "");
        }

        return slug;
    }

    private String normalizeRequiredName(String name) {

        if (name == null || name.isBlank()) {
            throw new InvalidRequestException("Organization name cannot be blank");
        }

        String normalizedName = name.trim();

        if (normalizedName.length() > 150) {
            throw new InvalidRequestException("Organization name cannot exceed " + "150 characters");
        }

        return normalizedName;
    }

    private String normalizeRequiredSlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new InvalidRequestException("Organization slug cannot be blank");
        }

        String normalizedSlug = slug
                .trim()
                .toLowerCase(Locale.ROOT);

        if (!normalizedSlug.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$")) {
            throw new InvalidRequestException("Organization slug can only contain " + "lowercase letters, numbers " + "and single hyphens");
        }

        if (normalizedSlug.length() > MAX_SLUG_LENGTH) {
            throw new InvalidRequestException("Organization slug cannot exceed " + MAX_SLUG_LENGTH + " characters"
            );
        }

        return normalizedSlug;
    }

    private void validateOrganizationId(Long organizationId) {
        if (organizationId == null || organizationId <= 0) {
            throw new InvalidRequestException("Organization ID must be greater than zero");
        }
    }

    private void validateCreateRequest(CreateOrganizationRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Create organization request cannot be null");
        }
    }

    private void validatePageable(Pageable pageable) {
        if (pageable == null) {
            throw new InvalidRequestException("Pageable cannot be null");
        }
    }
}