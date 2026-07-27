package com.ezra_anotida.invoice_maker.mapper;

import com.ezra_anotida.invoice_maker.dto.company.CompanyProfileResponse;
import com.ezra_anotida.invoice_maker.dto.company.CreateCompanyProfileRequest;
import com.ezra_anotida.invoice_maker.dto.company.UpdateCompanyProfileRequest;
import com.ezra_anotida.invoice_maker.entity.CompanyProfile;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CompanyProfileMapper {

    CompanyProfile toEntity(CreateCompanyProfileRequest request);

    CompanyProfileResponse toResponse(CompanyProfile company);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateCompanyProfileRequest request, @MappingTarget CompanyProfile entity);

}
