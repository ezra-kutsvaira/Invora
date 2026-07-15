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
    public void deleteCustomer(Long customerId) {
        Customer customer = findCustomerById(customerId);
        customerRepository.delete(customer);
    }

    @Override
    public List<CustomerResponse> searchCustomers(String keyword) {
        if(keyword == null || keyword.isEmpty()){
            return getAllCustomers();
        }
        List<Customer> customers = customerRepository.findByCustomerNameContainingIgnoreCase(keyword.trim());
        return customerMapper.toResponseList(customers);
    }

    private Customer findCustomerById(Long customerId) {
        if(customerId == null){
            throw new IllegalArgumentException("Customer id is null");
        }
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer with ID " + customerId + " not found"));
    }

    //Email Validation
    private void validateUniqueEmail(String email, Customer existingCustomer){
        if(email == null || email.isEmpty()){
            return;
        }

        boolean emailBelongsToCurrentCustomer = existingCustomer != null && existingCustomer.getEmail() != null && existingCustomer.getEmail().equalsIgnoreCase(email);
        if (!emailBelongsToCurrentCustomer && customerRepository.existsByEmail(email)) {

            throw new IllegalArgumentException(
                    "A customer with email " + email + " already exists"
            );
        }
    }

    private void validateUniquePhone(String phone,Customer existingCustomer) {
        if (phone == null || phone.isEmpty()) {
            return;
        }
        boolean phoneBelongsToCurrentCustomer = existingCustomer != null && existingCustomer.getPhone() != null && existingCustomer.getPhone().equals(phone);

        if (!phoneBelongsToCurrentCustomer && customerRepository.existsByPhoneNumber(phone)) {

            throw new IllegalArgumentException("A customer with phone number " + phone + " already exists"
            );
        }
    }
}
