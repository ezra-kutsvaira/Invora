package mapper;

import dto.currency.CreateCurrencyRequest;
import dto.currency.CurrencyResponse;
import dto.currency.UpdateCurrencyRequest;
import entity.Currency;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {

    Currency toEntity(CreateCurrencyRequest request);

    CurrencyResponse toResponse(Currency currency);

    List<CurrencyResponse> toResponseList(List<Currency> currencies);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateCurrencyRequest request, @MappingTarget Currency currency);


}
