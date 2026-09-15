package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.EmailVerificationToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT token
        FROM EmailVerificationToken token
        JOIN FETCH token.user
        WHERE token.tokenHash = :tokenHash
        """)
    Optional<EmailVerificationToken> findByTokenHashForUpdate (@Param("tokenHash") String tokenHash);

    @Modifying(clearAutomatically = true , flushAutomatically = true)
    @Query("""
    UPDATE EmailVerificationToken token
    SET token.usedAt = :invalidatedAt
    WHERE token.user.id = :userId
    AND token.usedAt is NULL
    
    """)
    int validateUnusedTokenForUser(@Param("userId") Long userId, @Param("invalidatedAt") Instant invalidatedAt);

    @Modifying
    @Query("""
    DELETE FROM EmailVerificationToken token 
    WHERE token.expiresAt < :cutoff
    """)
    int deleteExpiredBefore(@Param("cutoff") Instant cutoff);
}
