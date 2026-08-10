package com.ezra_anotida.invoice_maker.service.impl;

import com.ezra_anotida.invoice_maker.dto.currency.*;
import com.ezra_anotida.invoice_maker.entity.Currency;
import com.ezra_anotida.invoice_maker.entity.Organization;
import com.ezra_anotida.invoice_maker.enums.OrganizationStatus;
import com.ezra_anotida.invoice_maker.exception.*;
import com.ezra_anotida.invoice_maker.mapper.CurrencyMapper;
import com.ezra_anotida.invoice_maker.repository.CurrencyRepository;
import com.ezra_anotida.invoice_maker.repository.OrganizationRepository;
import com.ezra_anotida.invoice_maker.service.CurrencyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final OrganizationRepository organizationRepository;
    private final CurrencyMapper currencyMapper;

    public CurrencyServiceImpl(CurrencyRepository currencyRepository, OrganizationRepository organizationRepository, CurrencyMapper currencyMapper) {
        this.currencyRepository = currencyRepository;
        this.organizationRepository = organizationRepository;
        this.currencyMapper = currencyMapper;
    }

    @Override
    public CurrencyResponse createCurrency(Long organizationId, CreateCurrencyRequest request) {
        Organization organization = findActiveOrganization(organizationId);
        validateUniqueCode(organizationId, request.code(), null);
        Currency currency = currencyMapper.toEntity(request);
        currency.setOrganization(organization);
        currency.setCode(normalizeCode(request.code()));
        currency.setActive(currency.getActive() == null ? true : currency.getActive());
        currency.setDefaultCurrency(Boolean.TRUE.equals(currency.getDefaultCurrency()));
        if (Boolean.TRUE.equals(currency.getDefaultCurrency()) && !Boolean.TRUE.equals(currency.getActive()))
            throw new InvalidRequestException("An inactive currency cannot be default");
        if (Boolean.TRUE.equals(currency.getDefaultCurrency())) removeCurrentDefault(organizationId);
        return currencyMapper.toResponse(currencyRepository.save(currency));
    }

    @Override @Transactional(readOnly = true)
    public CurrencyResponse getCurrencyById(Long organizationId, Long currencyId) {
        return currencyMapper.toResponse(findCurrency(organizationId, currencyId));
    }

    @Override @Transactional(readOnly = true)
    public CurrencyResponse getCurrencyByCode(Long organizationId, String code) {
        findActiveOrganization(organizationId);
        String normalized = normalizeCode(code);
        Currency currency = currencyRepository.findByOrganizationIdAndCodeIgnoreCase(organizationId, normalized)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", "code", normalized));
        return currencyMapper.toResponse(currency);
    }

    @Override @Transactional(readOnly = true)
    public CurrencyResponse getDefaultCurrency(Long organizationId) {
        findActiveOrganization(organizationId);
        return currencyMapper.toResponse(currencyRepository.findByOrganizationIdAndDefaultCurrencyTrue(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("No default currency has been configured")));
    }

    @Override @Transactional(readOnly = true)
    public List<CurrencyResponse> getAllCurrencies(Long organizationId) {
        findActiveOrganization(organizationId);
        return currencyMapper.toResponseList(currencyRepository.findByOrganizationId(organizationId));
    }

    @Override
    public CurrencyResponse updateCurrency(Long organizationId, Long currencyId, UpdateCurrencyRequest request) {
        Currency currency = findCurrency(organizationId, currencyId);
        validateUniqueCode(organizationId, request.code(), currency);
        boolean becomingDefault = Boolean.TRUE.equals(request.defaultCurrency()) && !Boolean.TRUE.equals(currency.getDefaultCurrency());
        if (becomingDefault) removeCurrentDefault(organizationId);
        currencyMapper.updateEntityFromRequest(request, currency);
        if (currency.getCode() != null) currency.setCode(normalizeCode(currency.getCode()));
        if (Boolean.TRUE.equals(currency.getDefaultCurrency()) && !Boolean.TRUE.equals(currency.getActive()))
            throw new InvalidResourceStateException("An inactive currency cannot be default");
        return currencyMapper.toResponse(currencyRepository.save(currency));
    }

    @Override
    public CurrencyResponse setDefaultCurrency(Long organizationId, Long currencyId) {
        Currency currency = findCurrency(organizationId, currencyId);
        if (!Boolean.TRUE.equals(currency.getActive())) throw new InvalidResourceStateException("An inactive currency cannot be default");
        if (!Boolean.TRUE.equals(currency.getDefaultCurrency())) {
            removeCurrentDefault(organizationId);
            currency.setDefaultCurrency(true);
            currencyRepository.save(currency);
        }
        return currencyMapper.toResponse(currency);
    }

    @Override
    public void deactivateCurrency(Long organizationId, Long currencyId) {
        Currency currency = findCurrency(organizationId, currencyId);
        if (Boolean.TRUE.equals(currency.getDefaultCurrency())) throw new InvalidResourceStateException("The default currency cannot be deactivated");
        if (!Boolean.TRUE.equals(currency.getActive())) throw new InvalidResourceStateException("Currency is already inactive");
        currency.setActive(false);
        currencyRepository.save(currency);
    }

    private Currency findCurrency(Long organizationId, Long currencyId) {
        findActiveOrganization(organizationId);
        validateId(currencyId, "Currency");
        return currencyRepository.findByIdAndOrganizationId(currencyId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", "id", currencyId));
    }

    private Organization findActiveOrganization(Long organizationId) {
        validateId(organizationId, "Organization");
        return organizationRepository.findByIdAndStatus(organizationId, OrganizationStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active organization", "id", organizationId));
    }

    private void validateUniqueCode(Long organizationId, String code, Currency existing) {
        if (code == null || code.isBlank()) return;
        String normalized = normalizeCode(code);
        boolean unchanged = existing != null && existing.getCode() != null && existing.getCode().equalsIgnoreCase(normalized);
        if (!unchanged && currencyRepository.existsByOrganizationIdAndCodeIgnoreCase(organizationId, normalized))
            throw new DuplicateResourceException("Currency", "code", normalized);
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) throw new InvalidRequestException("Currency code cannot be empty");
        return code.trim().toUpperCase();
    }

    private void removeCurrentDefault(Long organizationId) {
        currencyRepository.findByOrganizationIdAndDefaultCurrencyTrue(organizationId).ifPresent(current -> {
            current.setDefaultCurrency(false);
            currencyRepository.save(current);
        });
    }

    private void validateId(Long id, String resource) {
        if (id == null || id <= 0) throw new InvalidRequestException(resource + " id must be greater than zero");
    }
}
