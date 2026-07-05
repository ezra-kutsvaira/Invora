package repository;

import entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface AuditLogRepository  extends JpaRepository <AuditLog,Long> {

    List<AuditLog> findByEntityName (String entityName);

    List<AuditLog> findByEntityNameAndEntityId(String entityName, Long entityId);

    List<AuditLog> findPerformedBy (String performedBy);
}
