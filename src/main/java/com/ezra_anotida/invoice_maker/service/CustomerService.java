package com.ezra_anotida.invoice_maker.service;

import com.ezra_anotida.invoice_maker.dto.customer.CreateCustomerRequest;
import com.ezra_anotida.invoice_maker.dto.customer.CustomerResponse;
import com.ezra_anotida.invoice_maker.dto.customer.CustomerSummaryResponse;
import com.ezra_anotida.invoice_maker.dto.customer.UpdateCustomerRequest;
import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CreateCustomerRequest request);

    CustomerResponse getCustomerById(Long customerId);

    List<CustomerResponse> getAllCustomers();

    List<CustomerSummaryResponse> getCustomerSummaries();

    CustomerResponse updateCustomer(Long customerId, UpdateCustomerRequest request);

    void deleteCustomer(Long customerId);

    List<CustomerResponse> searchCustomers(String keyword);


}
