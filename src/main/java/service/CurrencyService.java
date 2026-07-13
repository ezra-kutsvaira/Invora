package service;

import dto.currency.CreateCurrencyRequest;
import dto.currency.CurrencyResponse;
import dto.currency.UpdateCurrencyRequest;

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
