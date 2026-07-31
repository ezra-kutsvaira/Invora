package com.ezra_anotida.invoice_maker.service.impl;

import com.ezra_anotida.invoice_maker.exception.DuplicateResourceException;
import com.ezra_anotida.invoice_maker.exception.InvalidRequestException;
import com.ezra_anotida.invoice_maker.exception.InvalidResourceStateException;
import com.ezra_anotida.invoice_maker.exception.ResourceNotFoundException;
import com.ezra_anotida.invoice_maker.dto.customer.CreateCustomerRequest;
import com.ezra_anotida.invoice_maker.dto.customer.CustomerResponse;
import com.ezra_anotida.invoice_maker.dto.customer.CustomerSummaryResponse;
import com.ezra_anotida.invoice_maker.dto.customer.UpdateCustomerRequest;
import com.ezra_anotida.invoice_maker.entity.Customer;
import com.ezra_anotida.invoice_maker.mapper.CustomerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ezra_anotida.invoice_maker.repository.CustomerRepository;
import com.ezra_anotida.invoice_maker.service.CustomerService;

import java.util.List;


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
        validateUniqueEmail(request.email(),null);
        validateUniquePhone(request.phone(), null);

        Customer customer  = customerMapper.toEntity(request);
        Customer savedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long customerId) {
        Customer customer = findCustomerById(customerId);

        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {

        //returning only active customers
        List<Customer> customers = customerRepository.findByActiveTrue();

        return customerMapper.toResponseList(customers);

    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerSummaryResponse> getCustomerSummaries() {
        List<Customer> customers = customerRepository.findByActiveTrue();
        return customerMapper.toSummaryResponseList(customers);
    }

    @Override
    public CustomerResponse updateCustomer(Long customerId, UpdateCustomerRequest request) {
        Customer existingCustomer = findCustomerById(customerId);

        validateUniqueEmail(request.email(), existingCustomer);

        validateUniquePhone(request.phone(), existingCustomer);

        customerMapper.updateEntityFromRequest(request,existingCustomer);

        Customer updatedCustomer = customerRepository.save(existingCustomer);

        return customerMapper.toResponse(updatedCustomer);
    }

    @Override
    public void deactivateCustomer(Long customerId) {

        Customer customer = findCustomerById(customerId);

        if(!Boolean.TRUE.equals(customer.getActive())){
            throw new InvalidResourceStateException("Customer is already inactive");
        }

        customer.setActive(false);

        customerRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> searchCustomers(String keyword) {

        if(keyword == null || keyword.isBlank()){
            return getAllCustomers();
        }
        List<Customer> customers = customerRepository.findByActiveTrueAndCustomerNameContainingIgnoreCase(keyword.trim());

        return customerMapper.toResponseList(customers);
    }

    @Override
    public CustomerResponse reactivateCustomer(Long customerId) {

        Customer customer = findCustomerById(customerId);

        if(Boolean.TRUE.equals(customer.getActive())){
            throw new InvalidResourceStateException("Customer is already active");
        }

        customer.setActive(true);

        Customer reactivatedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(reactivatedCustomer);
    }

    @Override
    public List<CustomerResponse> getInActiveCustomers() {

       List<Customer> customers = customerRepository.findActiveFalse();

        return customerMapper.toResponseList(customers);
    }

    private Customer findCustomerById(Long customerId) {
        if(customerId == null || customerId <= 0){
            throw new InvalidRequestException("Customer ID cannot be null");
        }
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
    }

    //Email Validation
    private void validateUniqueEmail(String email, Customer existingCustomer){
        if(email == null || email.isBlank()){
            return;
        }

        boolean emailBelongsToCurrentCustomer = existingCustomer != null && existingCustomer.getEmail() != null && existingCustomer.getEmail().equalsIgnoreCase(email);
        if (!emailBelongsToCurrentCustomer && customerRepository.existsByEmailIgnoreCase(normalizedEmail(email))) {

            throw new DuplicateResourceException("Customer", "email", email);
        }
    }

    private void validateUniquePhone(String phone,Customer existingCustomer) {
        if (phone == null || phone.isEmpty()) {
            return;
        }
        boolean phoneBelongsToCurrentCustomer = existingCustomer != null && existingCustomer.getPhone() != null && existingCustomer.getPhone().equals(phone);

        if (!phoneBelongsToCurrentCustomer && customerRepository.existsByPhoneNumber(phone)) {

            throw new DuplicateResourceException("Customer", "phone number", phone);
        }
    }

    private String normalizedEmail(String email){
        if(email == null || email.isBlank()){
            return null;
        }
        return email.trim().toLowerCase();
    }
}
