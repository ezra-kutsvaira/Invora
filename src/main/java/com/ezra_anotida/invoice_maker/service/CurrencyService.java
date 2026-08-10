package com.ezra_anotida.invoice_maker.service;

import com.ezra_anotida.invoice_maker.dto.currency.*;
import java.util.List;

public interface CurrencyService {
    CurrencyResponse createCurrency(Long organizationId, CreateCurrencyRequest request);
    CurrencyResponse getCurrencyById(Long organizationId, Long currencyId);
    CurrencyResponse getCurrencyByCode(Long organizationId, String currencyCode);
    CurrencyResponse getDefaultCurrency(Long organizationId);
    List<CurrencyResponse> getAllCurrencies(Long organizationId);
    CurrencyResponse updateCurrency(Long organizationId, Long currencyId, UpdateCurrencyRequest request);
    CurrencyResponse setDefaultCurrency(Long organizationId, Long currencyId);
    void deactivateCurrency(Long organizationId, Long currencyId);
}
