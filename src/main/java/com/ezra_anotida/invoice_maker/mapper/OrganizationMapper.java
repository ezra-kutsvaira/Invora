package com.ezra_anotida.invoice_maker.mapper;

import com.ezra_anotida.invoice_maker.dto.organization.CreateOrganizationRequest;
import com.ezra_anotida.invoice_maker.dto.organization.OrganizationResponse;
import com.ezra_anotida.invoice_maker.dto.organization.UpdateOrganizationRequest;
import com.ezra_anotida.invoice_maker.entity.Organization;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "companyProfile", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Organization toEntity(
            CreateOrganizationRequest request
    );

    @Mapping(
            target = "companyProfileConfigured",
            expression = "java(organization.getCompanyProfile() != null)"
    )
    OrganizationResponse toResponse(
            Organization organization
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "companyProfile", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    void updateEntityFromRequest(
            UpdateOrganizationRequest request,
            @MappingTarget Organization organization
    );
}