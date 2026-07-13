package service;

import dto.customer.CreateCustomerRequest;
import dto.customer.CustomerResponse;
import dto.customer.CustomerSummaryResponse;
import dto.customer.UpdateCustomerRequest;
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
