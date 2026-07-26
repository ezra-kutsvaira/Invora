package service.impl;

import com.ezra_anotida.invoice_maker.exception.DuplicateResourceException;
import com.ezra_anotida.invoice_maker.exception.InvalidRequestException;
import com.ezra_anotida.invoice_maker.exception.InvalidResourceStateException;
import com.ezra_anotida.invoice_maker.exception.ResourceNotFoundException;
import dto.currency.CreateCurrencyRequest;
import dto.currency.CurrencyResponse;
import dto.currency.UpdateCurrencyRequest;
import entity.Currency;
import jakarta.transaction.Transactional;
import mapper.CurrencyMapper;
import org.springframework.stereotype.Service;
import repository.CurrencyRepository;
import service.CurrencyService;
import java.util.List;

@Service
@Transactional
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    public CurrencyServiceImpl(CurrencyRepository currencyRepository, CurrencyMapper currencyMapper) {
        this.currencyRepository = currencyRepository;
        this.currencyMapper = currencyMapper;
    }


    @Override
    public CurrencyResponse createCurrency(CreateCurrencyRequest request) {

        validateAlreadyExistingCurrency(request.code(), null);

        Currency currency = currencyMapper.toEntity(request);

        currency.setCode(request.code().trim().toUpperCase());

        if(currency.getActive() == null){
            currency.setActive(true);
        }

        if(currency.getDefaultCurrency() == null){
            currency.setDefaultCurrency(false);
        }

        //Setting a new currency as a default, remove the old currency
        if(Boolean.TRUE.equals(currency.getDefaultCurrency())){
            removeCurrentDefaultCurrency();
        }

        Currency savedCurrency = currencyRepository.save(currency);

        return currencyMapper.toResponse(savedCurrency);
    }

    @Override
    public CurrencyResponse getCurrencyById(Long currencyId) {

        Currency currency = findCurrencyById(currencyId);

        return currencyMapper.toResponse(currency);
    }

    @Override
    public CurrencyResponse getCurrencyByCode(String currencyCode) {

        Currency currency = findCurrencyByCode(currencyCode);

        return currencyMapper.toResponse(currency);
    }

    @Override
    public CurrencyResponse getDefaultCurrency() {

        Currency currency = currencyRepository
                .findByDefaultCurrencyTrue()
                .orElseThrow(() -> new ResourceNotFoundException("No default currency has been configured"));

        return currencyMapper.toResponse(currency);
    }

    @Override
    public List<CurrencyResponse> getAllCurrencies() {

        List<Currency> currencies = currencyRepository.findAll();

        return currencyMapper.toResponseList(currencies);
    }

    @Override
    public CurrencyResponse updateCurrency(Long currencyId, UpdateCurrencyRequest request) {

        Currency existingCurrency = findCurrencyById(currencyId);

        validateAlreadyExistingCurrency(request.code(), existingCurrency);

        boolean becomingDefault = Boolean.TRUE.equals(request.defaultCurrency()) && !Boolean.TRUE.equals(existingCurrency.getDefaultCurrency());

        if(becomingDefault){
            removeCurrentDefaultCurrency();
        }

        currencyMapper.updateEntityFromRequest(request, existingCurrency);

        //Normalisation
        if(existingCurrency.getCode() != null){
            existingCurrency.setCode(
                    existingCurrency.getCode()
                            .trim()
                            .toUpperCase()
            );
        }

        Currency updatedCurrency = currencyRepository.save(existingCurrency);

        return currencyMapper.toResponse(updatedCurrency);
    }

    @Override
    public CurrencyResponse setDefaultCurrency(Long currencyId) {

        Currency currency = findCurrencyById(currencyId);

        if (!Boolean.TRUE.equals(currency.getActive())) {
            throw new InvalidResourceStateException("An inactive currency cannot be set as default");
        }

        if (Boolean.TRUE.equals(currency.getDefaultCurrency())) {
            return currencyMapper.toResponse(currency);
        }

        removeCurrentDefaultCurrency();

        currency.setDefaultCurrency(true);

        Currency updatedCurrency = currencyRepository.save(currency);

        return currencyMapper.toResponse(updatedCurrency);
    }

    @Override
    public void deleteCurrency(Long currencyId) {

        Currency currency = findCurrencyById(currencyId);

        if(Boolean.TRUE.equals(currency.getDefaultCurrency())){
            throw new InvalidResourceStateException("The default currency cannot be deleted");
        }

        currencyRepository.delete(currency);

    }

    private Currency findCurrencyById(Long currencyId) {

        if(currencyId == null){
            throw new InvalidRequestException("Currency ID cannot be null");
        }

        return currencyRepository.findById(currencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", "id", currencyId));
    }


    private void validateAlreadyExistingCurrency (String currencyCode, Currency existingCurrency){

        if(currencyCode == null || currencyCode.isBlank()){
            return;
        }

        String normalizedCode = currencyCode.trim().toUpperCase();

        boolean codeBelongsToCurrency = existingCurrency != null && existingCurrency.getCode() != null && existingCurrency.getCode().equalsIgnoreCase(normalizedCode);

        if(!codeBelongsToCurrency &&  currencyRepository.existsByCodeIgnoreCase(normalizedCode)){
            throw new DuplicateResourceException("Currency", "code", normalizedCode);
        }

    }

    private Currency findCurrencyByCode(String currencyCode) {

        if (currencyCode == null || currencyCode.isBlank()){
            throw new InvalidRequestException("Currency code cannot be empty");
        }

        String normalizedCode = currencyCode.trim().toUpperCase();

        return currencyRepository.findByCodeIgnoreCase(normalizedCode)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", "code", normalizedCode));
    }

    private void removeCurrentDefaultCurrency() {

        currencyRepository.findByDefaultCurrencyTrue()
                .ifPresent(currentDefault -> {
                    currentDefault.setDefaultCurrency(false);
                    currencyRepository.save(currentDefault);
                });
    }

}
