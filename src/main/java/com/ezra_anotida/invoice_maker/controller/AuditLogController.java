package com.ezra_anotida.invoice_maker.controller;

import com.ezra_anotida.invoice_maker.dto.audit.AuditLogResponse;
import com.ezra_anotida.invoice_maker.service.AuditLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/organizations/{organizationId}/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/{auditLogId}")
    public ResponseEntity<AuditLogResponse> getAuditLogById(
            @PathVariable("organizationId") Long organizationId,
            @PathVariable("auditLogId") Long auditLogId
    ) {
        AuditLogResponse auditLog =
                auditLogService.getAuditLogById(organizationId, auditLogId);

        return ResponseEntity.ok(auditLog);
    }

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAllAuditLogs(
            @PathVariable("organizationId") Long organizationId
    ) {
        List<AuditLogResponse> auditLogs =
                auditLogService.getAllAuditLogs(organizationId);

        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/entity")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByEntity(
            @PathVariable("organizationId") Long organizationId,
            @RequestParam("entityType") String entityType,
            @RequestParam("entityId") Long entityId
    ) {
        List<AuditLogResponse> auditLogs =
                auditLogService.getAuditLogsByEntity(
                        organizationId, entityType, entityId
                );

        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/action")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByAction(
            @PathVariable("organizationId") Long organizationId,
            @RequestParam("action") String action
    ) {
        List<AuditLogResponse> auditLogs =
                auditLogService.getAuditLogsByAction(organizationId, action);

        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/between")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsBetween(@PathVariable("organizationId") Long organizationId,

            @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam("endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate
    ) {
        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startDate must be before or equal to endDate");
        }

        List<AuditLogResponse> auditLogs = auditLogService.getAuditLogsBetween(organizationId, startDate, endDate);

        return ResponseEntity.ok(auditLogs);
    }
}