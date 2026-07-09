package mapper;

import dto.customer.CreateCustomerRequest;
import dto.customer.CustomerResponse;
import dto.customer.CustomerSummaryResponse;
import dto.customer.UpdateCustomerRequest;
import entity.Customer;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel= "spring")
public interface CustomerMapper {

    //start with the one you are converting into
    Customer toEntity(CreateCustomerRequest request);

    CustomerResponse toResponse(Customer customer);

    CustomerSummaryResponse toSummaryResponse(Customer customer);

    List<CustomerResponse> toResponseList(List<Customer> customers);

    List<CustomerSummaryResponse> toSummaryResponseList(List<Customer> customers);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateCustomerRequest request, @MappingTarget Customer customer);
}
