package mapper;

import dto.company.CompanyProfileResponse;
import dto.company.CreateCompanyProfileRequest;
import dto.company.UpdateCompanyProfileRequest;
import entity.CompanyProfile;
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
