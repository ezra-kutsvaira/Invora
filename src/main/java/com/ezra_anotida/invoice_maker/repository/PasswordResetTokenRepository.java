package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.PasswordResetToken;
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
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT token
            FROM PasswordResetToken token
            JOIN FETCH token.user
            WHERE token.tokenHash = :tokenHash
            """)
    Optional <PasswordResetToken> findByTokenHashForUpdate(@Param("tokenHash") String tokenHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE PasswordResetToken token
            SET token.usedAt = :invalidatedAt
            WHERE token.user.id = :userId
              AND token.usedAt IS NULL
            """)
    int invalidateUnusedTokensForUser(@Param("userId")Long userId, @Param("invalidatedAt") Instant invalidatedAt);

    @Modifying
    @Query("""
            DELETE FROM PasswordResetToken token
            WHERE token.expiresAt < :cutoff
            """)
    int deleteExpiredBefore(@Param("cutoff") Instant cutoff);

}
