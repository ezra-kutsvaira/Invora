package com.ezra_anotida.invoice_maker.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Clock;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SecurityProperties.class)
public class JwtConfig {

    /*
    JWT Encoder and Decoder
    RSA Signing and Verification Keys
    Issuer Validation
    Audience Validation
    Token Timestamp Validation
     */

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN_TYPE = "access";

    @Bean
    public Clock clock(){
        return Clock.systemUTC();
    }

    @Bean
    public RSAPublicKey jwtPublicKey(SecurityProperties securityProperties){
        return readPublicKey(securityProperties.rsa().publicKey());
    }

    @Bean
    public RSAPrivateKey jwtPrivateKey(SecurityProperties securityProperties){
        return readPrivateKey(securityProperties.rsa().privateKey());
    }

    @Bean
    public JwtEncoder jwtEncoder(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey){
        return NimbusJwtEncoder
                .withKeyPair(rsaPublicKey, rsaPrivateKey)
                .algorithm(SignatureAlgorithm.RS256)
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey rsaPublicKey, SecurityProperties securityProperties){

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withPublicKey(rsaPublicKey)
                .signatureAlgorithm(SignatureAlgorithm.RS256)
                .build();

        OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(securityProperties.issuer());

        OAuth2TokenValidator<Jwt> audienceValidator =
                new JwtClaimValidator<List<String>>(
                JwtClaimNames.AUD, audiences -> audiences != null && audiences.contains(securityProperties.audience()));

        OAuth2TokenValidator<Jwt> tokenTypeValidator = new JwtClaimValidator<String>(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE::equals);

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator, tokenTypeValidator));

        return decoder;
    }

    public RSAPublicKey readPublicKey(Resource resource) {
        try(InputStream inputStream = resource.getInputStream()){

            RSAPublicKey publicKey = RsaKeyConverters.x509().convert(inputStream);

            if(publicKey == null){
                throw new IllegalStateException("RSA public-key conversion returned null");
            }

            return publicKey;
        }catch (IOException | IllegalArgumentException exception){
            throw new IllegalStateException("Unable to load RSA public key from " + resource.getDescription(), exception);
        }

    }

    public RSAPrivateKey readPrivateKey(Resource resource) {
        try(InputStream inputStream = resource.getInputStream()){

            RSAPrivateKey privateKey = RsaKeyConverters.pkcs8().convert(inputStream);

            if(privateKey == null){
                throw new IllegalStateException("RSA private-key conversion returned null");
            }

            return privateKey;

        }catch (IOException | IllegalArgumentException exception){
            throw new IllegalStateException("Unable to load RSA private key from " + resource.getDescription(), exception);
        }

    }



}
