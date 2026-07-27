package com.ezra_anotida.invoice_maker.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

    @Entity
    @Table(name = "audit_logs")
    public class AuditLog {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank
        @Size(max = 100)
        @Column(nullable = false)
        private String action;

        @NotBlank
        @Size(max = 100)
        @Column(nullable = false)
        private String entityName;

        private Long entityId;

        @Column(length = 2000)
        private String description;

        @Size(max = 255)
        private String performedBy;

        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;

        public AuditLog() {
        }

        @PrePersist
        public void prePersist() {
            createdAt = LocalDateTime.now();
        }

        public Long getId() {
            return id;
        }

        public String getAction() {
            return action;
        }

        public String getEntityName() {
            return entityName;
        }

        public Long getEntityId() {
            return entityId;
        }

        public String getDescription() {
            return description;
        }

        public String getPerformedBy() {
            return performedBy;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public void setEntityName(String entityName) {
            this.entityName = entityName;
        }

        public void setEntityId(Long entityId) {
            this.entityId = entityId;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setPerformedBy(String performedBy) {
            this.performedBy = performedBy;
        }
    }

