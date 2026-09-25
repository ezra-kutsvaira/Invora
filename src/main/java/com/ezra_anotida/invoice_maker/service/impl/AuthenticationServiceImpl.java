package com.ezra_anotida.invoice_maker.service.impl;

import com.ezra_anotida.invoice_maker.dto.auth.AuthenticationResponse;
import com.ezra_anotida.invoice_maker.dto.auth.LoginRequest;
import com.ezra_anotida.invoice_maker.dto.auth.RefreshTokenRequest;
import com.ezra_anotida.invoice_maker.security.authentication.InvoraUserDetails;
import com.ezra_anotida.invoice_maker.security.jwt.JwtTokenService;
import com.ezra_anotida.invoice_maker.security.token.IssuedRefreshToken;
import com.ezra_anotida.invoice_maker.security.token.RotatedRefreshToken;
import com.ezra_anotida.invoice_maker.service.AuthenticationService;
import com.ezra_anotida.invoice_maker.service.RefreshTokenService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public AuthenticationResponse login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.email(), loginRequest.password()));

        InvoraUserDetails principal = (InvoraUserDetails) authentication.getPrincipal();

        JwtTokenService.IssuedAccessToken accessToken = jwtTokenService.generateAccessToken(principal);

        IssuedRefreshToken refreshToken = refreshTokenService.issueToken(principal.getUserId());

        return createAuthenticationResponse(accessToken, refreshToken);
    }

    
    @Override
    public AuthenticationResponse refresh(RefreshTokenRequest refreshTokenRequest) {

        RotatedRefreshToken rotatedToken = refreshTokenService.rotate(refreshTokenRequest.refreshToken());

        JwtTokenService.IssuedAccessToken accessToken = jwtTokenService.generateAccessToken(rotatedToken.userId());

        return createAuthenticationResponse(accessToken, rotatedToken.issuedRefreshToken());
    }

    @Override
    public void logout(String rawRefreshToken) {

        refreshTokenService.revoke(rawRefreshToken);

    }

    private AuthenticationResponse createAuthenticationResponse(JwtTokenService.IssuedAccessToken accessToken, IssuedRefreshToken refreshToken) {

        return new AuthenticationResponse(
                accessToken.value(),
                accessToken.type(),
                accessToken.expiresInSeconds(),
                refreshToken.value()
        );
    }
}
