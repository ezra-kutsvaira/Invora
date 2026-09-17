package com.ezra_anotida.invoice_maker.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "invora.security")
public record SecurityProperties(

            /*
    Issuer
    Audience
    Access-token lifetime
    Refresh-token lifetime
    Allowed Frontend Origins
    Verification-token lifetime
    Password-reset-token lifetime
     */
        @NotBlank(message = "JWT issuer is required")
        String issuer,

        @NotBlank(message = "JWT audience is required")
        String audience,

        @NotNull(message = "Access-token lifetime is required")
        Duration accessTokenTtl,

        @NotNull(message = "Refresh-token lifetime is required")
        Duration refreshTokenTtl,

        @NotNull(message = "Email-verification-token lifetime is required")
        Duration emailVerificationTokenTtl,

        @NotNull(message = "Password-reset-token lifetime is required")
        Duration passwordResetTokenTtl,

        @Valid
        @NotNull(message = "RSA key configuration is required")
        Rsa rsa) {

    public SecurityProperties{
        validatePositive(accessTokenTtl, "accessTokenTtl");
        validatePositive(refreshTokenTtl, "refreshTokenTtl");
        validatePositive(emailVerificationTokenTtl, "emailVerificationTokenTtl");
        validatePositive(passwordResetTokenTtl, "passwordResetTokenTtl");

    }

    private static void validatePositive(@NotNull(message = "Access-token lifetime is required") Duration duration, String propertyName) {

        if(duration != null && (duration.isZero() || duration.isNegative())){
            throw new IllegalArgumentException(propertyName + "must be greater than zero");
        }
    }

    public record Rsa(

        @NotNull(message = "RSA public key is required")
        Resource publicKey,

        @NotNull(message = "RSA private key is required")
        Resource privateKey
    ){}
}
