package com.ezra_anotida.invoice_maker.service;

import com.ezra_anotida.invoice_maker.dto.customer.*;
import java.util.List;

public interface CustomerService {
    CustomerResponse createCustomer(Long organizationId, CreateCustomerRequest request);

    CustomerResponse getCustomerById(Long organizationId, Long customerId);

    List<CustomerResponse> getAllCustomers(Long organizationId);

    List<CustomerSummaryResponse> getCustomerSummaries(Long organizationId);

    CustomerResponse updateCustomer(Long organizationId, Long customerId, UpdateCustomerRequest request);

    void deactivateCustomer(Long organizationId, Long customerId);

    List<CustomerResponse> searchCustomers(Long organizationId, String keyword);

    CustomerResponse reactivateCustomer(Long organizationId, Long customerId);

    List<CustomerResponse> getInactiveCustomers(Long organizationId);
}
