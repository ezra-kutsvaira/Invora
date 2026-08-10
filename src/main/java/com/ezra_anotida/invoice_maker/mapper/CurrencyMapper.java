package com.ezra_anotida.invoice_maker.mapper;

import com.ezra_anotida.invoice_maker.dto.currency.CreateCurrencyRequest;
import com.ezra_anotida.invoice_maker.dto.currency.CurrencyResponse;
import com.ezra_anotida.invoice_maker.dto.currency.UpdateCurrencyRequest;
import com.ezra_anotida.invoice_maker.entity.Currency;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {

    @Mapping(target = "organization", ignore = true)
    Currency toEntity(CreateCurrencyRequest request);

    CurrencyResponse toResponse(Currency currency);

    List<CurrencyResponse> toResponseList(List<Currency> currencies);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateCurrencyRequest request, @MappingTarget Currency currency);


}
