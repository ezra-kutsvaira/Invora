package com.ezra_anotida.invoice_maker.dto.audit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateAuditLogRequest(

        @NotBlank(message = "Action is required")
        @Size(max = 100, message = "Action cannot exceed 100")
        String action,

        @NotBlank(message = "Entity type is required")
        @Size(max = 100, message = "Action cannot exceed 100")
        String entityType,

        @Positive(message =  "Entity Id must be positive")
        Long entityId,

        @Size(max = 2000, message = "Details cannot exceed 2000 characters")
        String details,

        @Size(max = 25, message = "Performed by cannot exceed 250 characters")
        String performedBy
) {
}
