package com.ezra_anotida.invoice_maker.service.impl;

import com.ezra_anotida.invoice_maker.dto.audit.AuditLogResponse;
import com.ezra_anotida.invoice_maker.entity.AuditLog;
import com.ezra_anotida.invoice_maker.exception.InvalidRequestException;
import com.ezra_anotida.invoice_maker.exception.ResourceNotFoundException;
import com.ezra_anotida.invoice_maker.mapper.AuditLogMapper;
import com.ezra_anotida.invoice_maker.repository.AuditLogRepository;
import com.ezra_anotida.invoice_maker.service.AuditLogService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional

public class AuditLogServiceImpl implements AuditLogService {

    private static final int MAX_ACTION_LENGTH = 100;
    private static final int MAX_ENTITY_NAME_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 2000;

    private final AuditLogMapper auditLogMapper;
    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogMapper auditLogMapper, AuditLogRepository auditLogRepository) {
        this.auditLogMapper = auditLogMapper;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void logAction(String action, String entityType, Long entityId, String details) {

        String normalizedAction = validateAndNormalizeRequiredText(action, "Action" , MAX_ACTION_LENGTH);

        String normalizedEntityName = validateAndNormalizeRequiredText(entityType, "Entity type" , MAX_ENTITY_NAME_LENGTH);

        validateOptionalEntityId(entityId);

        String normalizedDescription  = normalizeOptionalText(details, "Details" , MAX_DESCRIPTION_LENGTH);

        AuditLog auditLog = new AuditLog();
        auditLog.setAction(normalizedAction);
        auditLog.setEntityName(normalizedEntityName);
        auditLog.setEntityId(entityId);
        auditLog.setDescription(normalizedDescription);
        auditLog.setPerformedBy("SYSTEM");

        auditLogRepository.save(auditLog);
    }


    @Override
    @Transactional(readOnly = true)
    public AuditLogResponse getAuditLogById(Long auditLogId) {

        AuditLog auditLog = findAuditLogById(auditLogId);

        return auditLogMapper.toResponse(auditLog);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAllAuditLogs() {

        List<AuditLog> auditLogs = auditLogRepository.findAll(Sort.by(Sort.Direction.DESC,"createdAt"));

        return auditLogMapper.toResponseList(auditLogs);
    }

    @Override
    public List<AuditLogResponse> getAuditLogsByEntity(String entityType, Long entityId) {

      String normalizedEntityName = validateAndNormalizeRequiredText(entityType, "Entity type" , MAX_ENTITY_NAME_LENGTH);

      validateRequiredId(entityId, "Entity ID");

      List<AuditLog> auditLogs = auditLogRepository.findByEntityNameIgnoreCaseAndEntityIdOrderByCreatedAtDesc(normalizedEntityName, entityId);

        return auditLogMapper.toResponseList(auditLogs);
    }



    @Override
    public List<AuditLogResponse> getAuditLogsByAction(String action) {

        String normalizedAction = validateAndNormalizeRequiredText(action, "Action", MAX_ACTION_LENGTH);

        List<AuditLog> auditLogs = auditLogRepository.findByActionIgnoreCaseOrderByCreatedAtDesc(normalizedAction);

        return auditLogMapper.toResponseList(auditLogs);
    }

    @Override
    public List<AuditLogResponse> getAuditLogsBetween(LocalDateTime startDate, LocalDateTime endDate) {

        validateDateRange(startDate, endDate);

        List<AuditLog> auditLogs = auditLogRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);

        return auditLogMapper.toResponseList(auditLogs);
    }


    //HELPER METHODS
    private AuditLog findAuditLogById(Long auditLogId) {
        validateRequiredId(auditLogId, "Audit Log Id");

        return auditLogRepository.findById(auditLogId)
                .orElseThrow(() -> new ResourceNotFoundException("AuditLog", "id", auditLogId));
    }


    private void validateRequiredId(Long id, String fieldName) {

        if(id == null){
            throw new InvalidRequestException(fieldName + "cannot be null");
        }

        if(id <= 0){
            throw new InvalidRequestException(fieldName );
        }

    }

    private void validateOptionalEntityId(Long entityId) {

        if(entityId != null && entityId <= 0){
            throw new InvalidRequestException("Entity Id must be greater than zero");
        }
    }

    private String validateAndNormalizeRequiredText(String value, String fieldName, int maximumLength) {

        if(value == null || value.isBlank()){
            throw new InvalidRequestException(fieldName + "cannot be null or blank");
        }

        String normalizedValue = value.trim();

        if (normalizedValue.length() > maximumLength) {
            throw new InvalidRequestException(fieldName + " cannot exceed " + maximumLength + " characters");
        }

        return normalizedValue;
    }


    private String normalizeOptionalText(String value, String fieldName, int maximumLength) {
        if (value == null || value.isBlank()) {
            throw new InvalidRequestException(
                    fieldName + " cannot be null or blank"
            );
        }

        String normalizedValue = value.trim();

        if (normalizedValue.length() > maximumLength) {
            throw new InvalidRequestException(
                    fieldName + " cannot exceed "
                            + maximumLength
                            + " characters"
            );
        }

        return normalizedValue;
    }


    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {

        if(startDate == null){
            throw new InvalidRequestException("Start Date cannot be null");
        }

        if(endDate == null){
            throw new InvalidRequestException("End Date cannot be null");
        }

        if(startDate.isAfter(endDate)){
            throw new InvalidRequestException("Start date cannot be after the end date");
        }

    }

}
