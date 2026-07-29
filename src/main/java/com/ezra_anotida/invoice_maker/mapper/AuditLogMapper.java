package com.ezra_anotida.invoice_maker.mapper;

import com.ezra_anotida.invoice_maker.dto.audit.AuditLogResponse;
import com.ezra_anotida.invoice_maker.dto.audit.CreateAuditLogRequest;
import com.ezra_anotida.invoice_maker.entity.AuditLog;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    AuditLogResponse toResponse(AuditLog auditLog);

    List<AuditLogResponse> toResponseList(List<AuditLog> auditLogs);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "action", source = "action")
    @Mapping(target = "entityName", source = "entityType")
    @Mapping(target = "entityId", source = "entityId")
    @Mapping(target = "description", source = "details")
    @Mapping(target = "performedBy", source = "performedBy")
    AuditLog toEntity(CreateAuditLogRequest createAuditLogRequest);
}
