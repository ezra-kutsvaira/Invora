package com.ezra_anotida.invoice_maker.service;

import com.ezra_anotida.invoice_maker.dto.currency.CreateCurrencyRequest;
import com.ezra_anotida.invoice_maker.dto.currency.CurrencyResponse;
import com.ezra_anotida.invoice_maker.dto.currency.UpdateCurrencyRequest;

import java.util.List;

public interface CurrencyService {

    CurrencyResponse createCurrency(CreateCurrencyRequest request);

    CurrencyResponse getCurrencyById(Long currencyId);

    CurrencyResponse getCurrencyByCode(String currencyCode);

    CurrencyResponse getDefaultCurrency();

    List<CurrencyResponse> getAllCurrencies();

    CurrencyResponse updateCurrency(Long currencyId, UpdateCurrencyRequest request);

    CurrencyResponse setDefaultCurrency(Long currencyId);

    void deleteCurrency(Long currencyId);
}
