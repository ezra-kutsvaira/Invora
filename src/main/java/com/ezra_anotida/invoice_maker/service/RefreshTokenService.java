package com.ezra_anotida.invoice_maker.service;

import com.ezra_anotida.invoice_maker.security.token.IssuedRefreshToken;
import com.ezra_anotida.invoice_maker.security.token.RotatedRefreshToken;

public interface RefreshTokenService {

    IssuedRefreshToken issueToken(Long userId);

    RotatedRefreshToken rotate(String rawRefreshToken);

    void revoke(String rawRefreshToken);

    void revokeAllForUser(Long userId);
}
 