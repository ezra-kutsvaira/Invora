package dto.audit;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,

        String action,

        String entityName,

        Long entityId,

        String description,

        String performedBy,

        LocalDateTime createdAt

        ) {}
