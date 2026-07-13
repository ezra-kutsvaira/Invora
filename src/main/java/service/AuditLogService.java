package service;

import dto.audit.AuditLogResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogService {
    void logAction(String action, String entityType, Long entityId, String details
    );

    AuditLogResponse getAuditLogById(Long auditLogId);

    List<AuditLogResponse> getAllAuditLogs();

    List<AuditLogResponse> getAuditLogsByEntity(String entityType, Long entityId
    );

    List<AuditLogResponse> getAuditLogsByAction(String action);

    List<AuditLogResponse> getAuditLogsBetween(LocalDateTime startDate, LocalDateTime endDate
    );
}
