package service.impl;

import com.ezra_anotida.invoice_maker.exception.DuplicateResourceException;
import com.ezra_anotida.invoice_maker.exception.InvalidRequestException;
import com.ezra_anotida.invoice_maker.exception.ResourceNotFoundException;
import dto.customer.CreateCustomerRequest;
import dto.customer.CustomerResponse;
import dto.customer.CustomerSummaryResponse;
import dto.customer.UpdateCustomerRequest;
import entity.Customer;
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
        Customer customer = findCustomerById(customerId);

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
        if(keyword == null || keyword.isBlank()){
            return getAllCustomers();
        }
        List<Customer> customers = customerRepository.findByCustomerNameContainingIgnoreCase(keyword.trim());
        return customerMapper.toResponseList(customers);
    }

    private Customer findCustomerById(Long customerId) {
        if(customerId == null){
            throw new InvalidRequestException("Customer ID cannot be null");
        }
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
    }

    //Email Validation
    private void validateUniqueEmail(String email, Customer existingCustomer){
        if(email == null || email.isEmpty()){
            return;
        }

        boolean emailBelongsToCurrentCustomer = existingCustomer != null && existingCustomer.getEmail() != null && existingCustomer.getEmail().equalsIgnoreCase(email);
        if (!emailBelongsToCurrentCustomer && customerRepository.existsByEmail(email)) {

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
}
