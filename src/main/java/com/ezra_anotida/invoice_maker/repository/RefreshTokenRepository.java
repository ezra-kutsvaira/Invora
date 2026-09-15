package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long >{

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT token
        FROM RefreshToken token
        JOIN FETCH token.user
        WHERE token.tokenHash = :tokenHash
        """)
    Optional<RefreshToken> findByTokenHashForUpdate(@Param("tokenHash") String tokenHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE RefreshToken token
            SET token.revokedAt = :revokedAt,
                token.revokedReason = :reason
            WHERE token.user.id = :userId
                 AND token.revokedAt IS NULL
                 AND token.expiresAt > :revokedAt
        
            """)
    int revokeAllActiveByUserId(@Param("userId") Long userId, @Param("revokedAt") Instant revokedAt, @Param("reason") String reason);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE RefreshToken token
            SET token.revokedAt = :revokedAt,
                token.revokedReason = :reason
            WHERE token.familyId = :familyId
                 AND token.revokedAt IS NULL
            """)
    int revokeActiveTokenFamily(@Param("familyId")UUID familyId, @Param("revokedAt") Instant revokedAt, @Param("reason")String reason);


    @Modifying
    @Query("""
          DELETE FROM RefreshToken token
          WHERE token.expiresAt < :cutoff
          """)
    int deleteExpiredBefore(@Param("cutoff") Instant cutoff);
}
