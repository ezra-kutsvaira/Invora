package com.ezra_anotida.invoice_maker.service.impl;

import com.ezra_anotida.invoice_maker.config.SecurityProperties;
import com.ezra_anotida.invoice_maker.entity.RefreshToken;
import com.ezra_anotida.invoice_maker.entity.User;
import com.ezra_anotida.invoice_maker.exception.security.InvalidRefreshTokenException;
import com.ezra_anotida.invoice_maker.exception.security.RefreshTokenReuseException;
import com.ezra_anotida.invoice_maker.repository.RefreshTokenRepository;
import com.ezra_anotida.invoice_maker.repository.UserRepository;
import com.ezra_anotida.invoice_maker.security.token.IssuedRefreshToken;
import com.ezra_anotida.invoice_maker.security.token.OpaqueTokenUtils;
import com.ezra_anotida.invoice_maker.security.token.RotatedRefreshToken;
import com.ezra_anotida.invoice_maker.service.RefreshTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final String ROTATED_REASON = "ROTATED";
    private static final String LOGOUT_REASON = "LOGOUT";
    private static final String REUSE_DETECTED_REASON = "REUSE DETECTED";

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final OpaqueTokenUtils opaqueTokenUtils;
    private final SecurityProperties securityProperties;
    private final Clock clock;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, OpaqueTokenUtils opaqueTokenUtils, SecurityProperties securityProperties, Clock clock) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.opaqueTokenUtils = opaqueTokenUtils;
        this.securityProperties = securityProperties;
        this.clock = clock;
    }


    @Override
    @Transactional
    public IssuedRefreshToken issueToken(Long userId) {

        Objects.requireNonNull(userId, "User id is required");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        return createToken(user, UUID.randomUUID());
    }



    @Override
    @Transactional
    public RotatedRefreshToken rotate(String rawRefreshToken) {

        if(rawRefreshToken == null || rawRefreshToken.isBlank()){
            throw new InvalidRefreshTokenException("Refresh token is required");
        }


        String tokenHash = opaqueTokenUtils.hashToken(rawRefreshToken);

        RefreshToken existingToken = refreshTokenRepository.findByTokenHashForUpdate(tokenHash)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        Instant now = clock.instant();

        if(existingToken.getRevokedAt() != null){
            refreshTokenRepository.revokeActiveTokenFamily(existingToken.getFamilyId(), now , REUSE_DETECTED_REASON);

            throw new RefreshTokenReuseException("Refresh token reuse detected");
        }

        if(!existingToken.getExpiresAt().isAfter(now)){

            existingToken.setRevokedAt(now);
            existingToken.setRevokedReason("EXPIRED");

            refreshTokenRepository.save(existingToken);

            throw new InvalidRefreshTokenException("Refresh token has expired");
        }

        User user = existingToken.getUser();

        existingToken.setRevokedAt(now);
        existingToken.setRevokedReason(ROTATED_REASON);

        refreshTokenRepository.save(existingToken);

        IssuedRefreshToken replacement = createToken(user, existingToken.getFamilyId());

        return new RotatedRefreshToken(user.getId(), replacement);
    }

    @Override
    @Transactional
    public void revoke(String rawRefreshToken) {

        if(rawRefreshToken == null || rawRefreshToken.isBlank()){
            return;
        }

        String tokenHash = opaqueTokenUtils.hashToken(rawRefreshToken);

        refreshTokenRepository.findByTokenHashForUpdate(tokenHash)
                .ifPresent(token ->{

                    if(token.getRevokedAt() == null){
                        token.setRevokedAt(clock.instant());
                        token.setRevokedReason(LOGOUT_REASON);

                        refreshTokenRepository.save(token);
                    }
                });

    }

    @Override
    @Transactional
    public void revokeAllForUser(Long userId) {
        refreshTokenRepository.revokeAllActiveByUserId(userId, clock.instant(), LOGOUT_REASON);
    }


    private IssuedRefreshToken createToken(User user, UUID familyId) {

        Instant issuedAt = clock.instant();

        Instant expiresAt = issuedAt.plus(securityProperties.refreshTokenTtl());

        String rawToken = opaqueTokenUtils.generateToken();

        String tokenHash = opaqueTokenUtils.hashToken(rawToken);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setFamilyId(familyId);
        refreshToken.setExpiresAt(expiresAt);

        refreshTokenRepository.save(refreshToken);

        return new IssuedRefreshToken(rawToken, expiresAt);
    }
}
