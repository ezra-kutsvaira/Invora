package service.impl;

import dto.customer.CreateCustomerRequest;
import dto.customer.CustomerResponse;
import dto.customer.CustomerSummaryResponse;
import dto.customer.UpdateCustomerRequest;
import entity.Customer;
import jakarta.persistence.EntityNotFoundException;
import mapper.CustomerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.CustomerRepository;
import service.CustomerService;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }


    @Override
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        //validation
        validateUniquEmail(request.email(),null);
        validateUniquePhone(request.phone(), null);

        Customer customer  = customerMapper.toEntity(request);
        Customer savedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found "));

        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customerMapper.toResponseList(customers);

    }

    @Override
    public List<CustomerSummaryResponse> getCustomerSummaries() {
        return List.of();
    }

    @Override
    public CustomerResponse updateCustomer(Long customerId, UpdateCustomerRequest request) {
        return null;
    }

    @Override
    public void deleteCustomer(Long customerId) {

    }

    @Override
    public List<CustomerResponse> searchCustomers(String keyword) {
        return List.of();
    }
}
