package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByEntityNameIgnoreCaseOrderByCreatedAtDesc(String entityName);

    List<AuditLog> findByEntityNameIgnoreCaseAndEntityIdOrderByCreatedAtDesc(String entityName, Long entityId);

    List<AuditLog> findByActionIgnoreCaseOrderByCreatedAtDesc(String action);

    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    List<AuditLog> findByPerformedByIgnoreCaseOrderByCreatedAtDesc(String performedBy);
}