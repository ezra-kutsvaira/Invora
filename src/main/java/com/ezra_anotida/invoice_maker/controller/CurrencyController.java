package com.ezra_anotida.invoice_maker.controller;

import com.ezra_anotida.invoice_maker.dto.currency.CreateCurrencyRequest;
import com.ezra_anotida.invoice_maker.dto.currency.CurrencyResponse;
import com.ezra_anotida.invoice_maker.dto.currency.UpdateCurrencyRequest;
import com.ezra_anotida.invoice_maker.service.CurrencyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/{organizationId}/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }


    //Create Currency
    @PostMapping
    public ResponseEntity<CurrencyResponse> createCurrency (@PathVariable ("organizationId") Long organizationId, @Valid @RequestBody CreateCurrencyRequest createCurrencyRequest){

        CurrencyResponse currency = currencyService.createCurrency(organizationId, createCurrencyRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(currency);
    }

    //Get All Currencies
    @GetMapping
    public ResponseEntity<List<CurrencyResponse>> getAllCurrencies (@PathVariable ("organizationId") Long organizationId){

        List<CurrencyResponse>  currencies = currencyService.getAllCurrencies(organizationId);

        return ResponseEntity.ok(currencies);
    }

    //Get Currency By Its ID
    @GetMapping("/{currencyId}")
    public ResponseEntity<CurrencyResponse> getCurrencyById (@PathVariable ("organizationId") Long organizationId, @PathVariable ("currencyId") Long currencyId){

        CurrencyResponse currency = currencyService.getCurrencyById(organizationId, currencyId);

        return ResponseEntity.ok(currency);
    }

    @GetMapping("/code/{currencyCode}")
    public ResponseEntity<CurrencyResponse> getCurrencyByCode (@PathVariable("organizationId") Long organizationId, @PathVariable ("currencyCode") String currencyCode){

        CurrencyResponse currency = currencyService.getCurrencyByCode(organizationId, currencyCode);

        return ResponseEntity.ok(currency);
    }

    //Get the organization's default currency
    @GetMapping("/default")
    public ResponseEntity<CurrencyResponse> getDefaultCurrency (@PathVariable ("organizationId") Long organizationId){

        CurrencyResponse defaultCurrency = currencyService.getDefaultCurrency(organizationId);

        return ResponseEntity.ok(defaultCurrency);
    }

    //Update Currency
    @PutMapping("/{currencyId}")
    public ResponseEntity<CurrencyResponse> updateCurrency (@PathVariable ("organizationId") Long organizationId, @PathVariable ("currencyId") Long currencyId, @Valid @RequestBody UpdateCurrencyRequest updateCurrencyRequest){

        CurrencyResponse updatedCurrency = currencyService.updateCurrency(organizationId, currencyId, updateCurrencyRequest);

        return ResponseEntity.ok(updatedCurrency);
    }

    @PatchMapping("/{currencyId}/default")
    public ResponseEntity<CurrencyResponse> setDefaultCurrency (@PathVariable ("organizationId") Long organizationId, @PathVariable ("currencyId") Long currencyId){

        CurrencyResponse currency = currencyService.setDefaultCurrency(organizationId, currencyId);

        return ResponseEntity.ok(currency);
    }

    @PatchMapping("/{currencyId}/deactivate")
    public ResponseEntity<Void> deactivateCurrency (@PathVariable ("organizationId") Long organizationId, @PathVariable ("currencyId") Long currencyId){

        currencyService.deactivateCurrency(organizationId, currencyId);

        return ResponseEntity.noContent().build();
    }
}
