package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Optional<AuditLog> findByIdAndOrganizationId(Long auditLogId, Long organizationId);
    List<AuditLog> findByOrganizationIdOrderByCreatedAtDesc(Long organizationId);
    List<AuditLog> findByOrganizationIdAndEntityNameIgnoreCaseAndEntityIdOrderByCreatedAtDesc(Long organizationId, String entityName, Long entityId);
    List<AuditLog> findByOrganizationIdAndActionIgnoreCaseOrderByCreatedAtDesc(Long organizationId, String action);
    List<AuditLog> findByOrganizationIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long organizationId, LocalDateTime startDate, LocalDateTime endDate);
}
