package com.ezra_anotida.invoice_maker.security.jwt;

import com.ezra_anotida.invoice_maker.config.SecurityProperties;
import com.ezra_anotida.invoice_maker.security.authentication.InvoraUserDetails;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class JwtTokenService {

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN_CLAIM = "access";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    private final JwtEncoder jwtEncoder;
    private final SecurityProperties securityProperties;
    private final Clock clock;

    public JwtTokenService(JwtEncoder jwtEncoder, SecurityProperties securityProperties, Clock clock) {
        this.jwtEncoder = jwtEncoder;
        this.securityProperties = securityProperties;
        this.clock = clock;
    }

    public IssuedAccessToken generateAccessToken(InvoraUserDetails principal){
        Objects.requireNonNull(principal, "Authenticated principal is required");

        if(!principal.isEnabled()){
            throw new IllegalStateException("Cannot issue an access token for an inactive user");
        }

        if(!principal.isAccountNonLocked()){
            throw new IllegalStateException("Cannot issue a token for a locked user");
        }

        return generateAccessToken(principal.getUserId());
    }

    //Useful Method during the refresh-token rotation
    //The refresh-token service must verify that the user is active before calling it
    public IssuedAccessToken generateAccessToken(Long userId) {

        Objects.requireNonNull(userId,"User id is required");

        Instant issuedAt = clock.instant();

        Instant expiredAt = issuedAt.plus(securityProperties.accessTokenTtl());

        JwsHeader jwsHeader = JwsHeader
                .with(SignatureAlgorithm.RS256)
                .type("JWT")
                .build();

        JwtClaimsSet jwsClaimSet = JwtClaimsSet
                .builder()
                .issuer(securityProperties.issuer())
                .subject(userId.toString())
                .audience(List.of(securityProperties.audience()))
                .issuedAt(issuedAt)
                .expiresAt(expiredAt)
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_CLAIM)
                .build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(jwsHeader, jwsClaimSet);

        Jwt jwt = jwtEncoder.encode(jwtEncoderParameters);

        return new IssuedAccessToken(
                jwt.getTokenValue(),
                BEARER_TOKEN_TYPE,
                issuedAt,
                expiredAt
        );
    }

    public record IssuedAccessToken(
            String value,
            String type,
            Instant issuedAt,
            Instant expiresAt
    ) {
        public long expiresInSeconds(){
            return expiresAt.getEpochSecond() - issuedAt.getEpochSecond();
        }
    }

}
