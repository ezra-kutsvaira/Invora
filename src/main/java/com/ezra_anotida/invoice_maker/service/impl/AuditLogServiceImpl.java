package com.ezra_anotida.invoice_maker.service.impl;

import com.ezra_anotida.invoice_maker.dto.audit.*;
import com.ezra_anotida.invoice_maker.entity.AuditLog;
import com.ezra_anotida.invoice_maker.entity.Organization;
import com.ezra_anotida.invoice_maker.enums.OrganizationStatus;
import com.ezra_anotida.invoice_maker.mapper.AuditLogMapper;
import com.ezra_anotida.invoice_maker.exception.InvalidRequestException;
import com.ezra_anotida.invoice_maker.exception.ResourceNotFoundException;
import com.ezra_anotida.invoice_maker.repository.AuditLogRepository;
import com.ezra_anotida.invoice_maker.repository.OrganizationRepository;
import com.ezra_anotida.invoice_maker.service.AuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private static final int MAX_ACTION_LENGTH = 100;

    private static final int MAX_ENTITY_NAME_LENGTH = 100;

    private static final int MAX_DESCRIPTION_LENGTH = 2000;

    private final AuditLogMapper mapper;

    private final AuditLogRepository repository;

    private final OrganizationRepository organizationRepository;

    public AuditLogServiceImpl(AuditLogMapper mapper, AuditLogRepository repository, OrganizationRepository organizationRepository) {
        this.mapper = mapper;
        this.repository = repository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    public void logAction(Long organizationId, String action, String entityType, Long entityId, String details) {

        Organization organization = findActiveOrganization(organizationId);

        String normalizedAction = requiredText(action, "Action", MAX_ACTION_LENGTH);

        String normalizedEntity = requiredText(entityType, "Entity type", MAX_ENTITY_NAME_LENGTH);

        if (entityId != null && entityId <= 0) {
            throw new InvalidRequestException("Entity id must be greater than zero");
        }

        String normalizedDetails = optionalText(details, "Details", MAX_DESCRIPTION_LENGTH);

        AuditLog log = mapper.toEntity(new CreateAuditLogRequest(normalizedAction, normalizedEntity, entityId, normalizedDetails, "SYSTEM"));

        log.setOrganization(organization);

        repository.save(log);
    }

    @Override @Transactional(readOnly = true)
    public AuditLogResponse getAuditLogById(Long organizationId, Long auditLogId) {

        return mapper.toResponse(findLog(organizationId, auditLogId));
    }

    @Override @Transactional(readOnly = true)
    public List<AuditLogResponse> getAllAuditLogs(Long organizationId) {

        findActiveOrganization(organizationId);

        return mapper.toResponseList(repository.findByOrganizationIdOrderByCreatedAtDesc(organizationId));
    }

    @Override @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByEntity(Long organizationId, String entityType, Long entityId) {

        findActiveOrganization(organizationId);

        String entity = requiredText(entityType, "Entity type", MAX_ENTITY_NAME_LENGTH);

        validateId(entityId, "Entity");

        return mapper.toResponseList(repository.findByOrganizationIdAndEntityNameIgnoreCaseAndEntityIdOrderByCreatedAtDesc(organizationId, entity, entityId));
    }

    @Override @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByAction(Long organizationId, String action) {

        findActiveOrganization(organizationId);

        String normalized = requiredText(action, "Action", MAX_ACTION_LENGTH);

        return mapper.toResponseList(repository.findByOrganizationIdAndActionIgnoreCaseOrderByCreatedAtDesc(organizationId, normalized));
    }

    @Override @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsBetween(Long organizationId, LocalDateTime startDate, LocalDateTime endDate) {

        findActiveOrganization(organizationId);

        if (startDate == null || endDate == null){
            throw new InvalidRequestException("Start and end dates are required");
        }

        if (startDate.isAfter(endDate)) {
            throw new InvalidRequestException("Start date cannot be after end date");
        }

        return mapper.toResponseList(repository.findByOrganizationIdAndCreatedAtBetweenOrderByCreatedAtDesc(organizationId, startDate, endDate));
    }

    private AuditLog findLog(Long organizationId, Long auditLogId) {

        findActiveOrganization(organizationId);

        validateId(auditLogId, "Audit log");

        return repository.findByIdAndOrganizationId(auditLogId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Audit log", "id", auditLogId));
    }

    private Organization findActiveOrganization(Long organizationId) {

        validateId(organizationId, "Organization");

        return organizationRepository.findByIdAndStatus(organizationId, OrganizationStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active organization", "id", organizationId));
    }

    private String requiredText(String value, String field, int max) {
        if (value == null || value.isBlank()){
            throw new InvalidRequestException(field + " cannot be blank");
        }

        String normalized = value.trim();

        if (normalized.length() > max){
            throw new InvalidRequestException(field + " cannot exceed " + max + " characters");
        }

        return normalized;
    }

    private String optionalText(String value, String field, int max) {

        if (value == null || value.isBlank()) return null;

        String normalized = value.trim();

        if (normalized.length() > max) {
            throw new InvalidRequestException(field + " cannot exceed " + max + " characters");
        }

        return normalized;
    }

    private void validateId(Long id, String resource) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException(resource + " id must be greater than zero");
        }
    }
}
