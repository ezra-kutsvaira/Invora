package com.ezra_anotida.invoice_maker.service;


import com.ezra_anotida.invoice_maker.dto.auth.AuthenticationResponse;
import com.ezra_anotida.invoice_maker.dto.auth.LoginRequest;
import com.ezra_anotida.invoice_maker.dto.auth.RefreshTokenRequest;

public interface AuthenticationService {

    AuthenticationResponse login (LoginRequest loginRequest);

    AuthenticationResponse refresh(RefreshTokenRequest refreshTokenRequest);

    void logout (String rawRefreshToken);



}
