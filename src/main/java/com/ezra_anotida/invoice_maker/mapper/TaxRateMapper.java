package com.ezra_anotida.invoice_maker.mapper;

import com.ezra_anotida.invoice_maker.dto.tax.CreateTaxRateRequest;
import com.ezra_anotida.invoice_maker.dto.tax.TaxRateResponse;
import com.ezra_anotida.invoice_maker.dto.tax.UpdateTaxRateRequest;
import com.ezra_anotida.invoice_maker.entity.TaxRate;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaxRateMapper {

    TaxRate toEntity(CreateTaxRateRequest request);

    TaxRateResponse toResponse(TaxRate taxRate);

    List<TaxRateResponse> toResponseList(List<TaxRate> taxRates);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void  updateToEntityFromRequest(UpdateTaxRateRequest request, @MappingTarget TaxRate entity);
}
