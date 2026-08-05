package com.ezra_anotida.invoice_maker.mapper;

import com.ezra_anotida.invoice_maker.dto.company.CompanyProfileResponse;
import com.ezra_anotida.invoice_maker.dto.company.CreateCompanyProfileRequest;
import com.ezra_anotida.invoice_maker.dto.company.UpdateCompanyProfileRequest;
import com.ezra_anotida.invoice_maker.entity.CompanyProfile;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CompanyProfileMapper {

    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CompanyProfile toEntity(CreateCompanyProfileRequest request);

    @Mapping(target = "organizationId", source = "organization.id")
    CompanyProfileResponse toResponse(CompanyProfile companyProfile);

    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateCompanyProfileRequest request, @MappingTarget CompanyProfile companyProfile);

}