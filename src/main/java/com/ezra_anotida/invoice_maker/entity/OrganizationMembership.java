package com.ezra_anotida.invoice_maker.entity;

import com.ezra_anotida.invoice_maker.enums.MembershipStatus;
import com.ezra_anotida.invoice_maker.enums.OrganizationRole;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "organization_memberships",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_organization_memberships_organization_user",
                columnNames = {"organization_id", "user_id"}
        ),
        indexes = {
                @Index(name = "idx_memberships_organization", columnList = "organization_id"),
                @Index(name = "idx_memberships_user", columnList = "user_id")
        }
)
public class OrganizationMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private OrganizationRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private MembershipStatus status = MembershipStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) status = MembershipStatus.ACTIVE;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Organization getOrganization() { return organization; }
    public User getUser() { return user; }
    public OrganizationRole getRole() { return role; }
    public MembershipStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setOrganization(Organization organization) { this.organization = organization; }
    public void setUser(User user) { this.user = user; }
    public void setRole(OrganizationRole role) { this.role = role; }
    public void setStatus(MembershipStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
