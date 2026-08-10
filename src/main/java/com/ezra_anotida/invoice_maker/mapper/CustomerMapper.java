package com.ezra_anotida.invoice_maker.mapper;

import com.ezra_anotida.invoice_maker.dto.customer.CreateCustomerRequest;
import com.ezra_anotida.invoice_maker.dto.customer.CustomerResponse;
import com.ezra_anotida.invoice_maker.dto.customer.CustomerSummaryResponse;
import com.ezra_anotida.invoice_maker.dto.customer.UpdateCustomerRequest;
import com.ezra_anotida.invoice_maker.entity.Customer;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel= "spring")
public interface CustomerMapper {

    //start with the one you are converting into
    @Mapping(target = "organization", ignore = true)
    Customer toEntity(CreateCustomerRequest request);

    CustomerResponse toResponse(Customer customer);

    CustomerSummaryResponse toSummaryResponse(Customer customer);

    List<CustomerResponse> toResponseList(List<Customer> customers);

    List<CustomerSummaryResponse> toSummaryResponseList(List<Customer> customers);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateCustomerRequest request, @MappingTarget Customer customer);
}
