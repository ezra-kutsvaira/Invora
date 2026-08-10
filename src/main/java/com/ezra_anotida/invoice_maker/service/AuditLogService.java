package com.ezra_anotida.invoice_maker.service;

import com.ezra_anotida.invoice_maker.dto.audit.AuditLogResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogService {
    void logAction(Long organizationId, String action, String entityType, Long entityId, String details);
    AuditLogResponse getAuditLogById(Long organizationId, Long auditLogId);
    List<AuditLogResponse> getAllAuditLogs(Long organizationId);
    List<AuditLogResponse> getAuditLogsByEntity(Long organizationId, String entityType, Long entityId);
    List<AuditLogResponse> getAuditLogsByAction(Long organizationId, String action);
    List<AuditLogResponse> getAuditLogsBetween(Long organizationId, LocalDateTime startDate, LocalDateTime endDate);
}
