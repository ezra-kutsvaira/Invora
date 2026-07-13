package mapper;

import dto.tax.CreateTaxRateRequest;
import dto.tax.TaxRateResponse;
import dto.tax.UpdateTaxRateRequest;
import entity.InvoiceItem;
import entity.TaxRate;
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
