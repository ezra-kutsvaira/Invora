package com.ezra_anotida.invoice_maker.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;


@Entity
@Table(
        name = "refresh_tokens",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_refresh_tokens_token_hash",
                        columnNames = "token_hash"
                )
        },

        indexes = {
                @Index(
                        name = "idx_refresh_token_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_refresh_tokens_family",
                        columnList = "family_id"
                ),
                @Index(
                        name = "idx_refresh_tokens_expires_at",
                        columnList = "expires_at"
                )
        }
)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_refresh_tokens_user"))
    private User user;

    @Column(name = "token_hash", nullable = false , length = 64)
    private String tokenHash;

    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    @Column(name = "issued_at" , nullable = false, updatable = false)
    private Instant issuedAt;

    @Column(name = "expires_at" , nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "revoked_reason", length = 100)
    private String revokedReason;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "replaced_by_token_id", foreignKey = @ForeignKey(name = "fk_refresh_tokens_replacement") )
    private RefreshToken replacedByToken;

    @Column(name  = "created_ip", length = 45)
    private String createdIp;

    @Column(name  = "user_agent", length = 512)
    private String userAgent;

    @PrePersist
    public void prePersist(){
        if(issuedAt == null){
            issuedAt = Instant.now();
        }
    }

    public boolean isRevoked(){
        return revokedAt != null;
    }

    public boolean isExpired(Instant now){
        return expiresAt == null || !expiresAt.isAfter(now);
    }

    public boolean isUsableAt(Instant now){
        return !isRevoked() && !isExpired(now);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public String getRevokedReason() {
        return revokedReason;
    }

    public void setRevokedReason(String revokedReason) {
        this.revokedReason = revokedReason;
    }

    public RefreshToken getReplacedByToken() {
        return replacedByToken;
    }

    public void setReplacedByToken(RefreshToken replacedByToken) {
        this.replacedByToken = replacedByToken;
    }

    public String getCreatedIp() {
        return createdIp;
    }

    public void setCreatedIp(String createdIp) {
        this.createdIp = createdIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

}
