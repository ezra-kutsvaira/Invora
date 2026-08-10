package com.ezra_anotida.invoice_maker.service.impl;

import com.ezra_anotida.invoice_maker.dto.customer.*;
import com.ezra_anotida.invoice_maker.entity.Customer;
import com.ezra_anotida.invoice_maker.entity.Organization;
import com.ezra_anotida.invoice_maker.enums.OrganizationStatus;
import com.ezra_anotida.invoice_maker.exception.*;
import com.ezra_anotida.invoice_maker.mapper.CustomerMapper;
import com.ezra_anotida.invoice_maker.repository.CustomerRepository;
import com.ezra_anotida.invoice_maker.repository.OrganizationRepository;
import com.ezra_anotida.invoice_maker.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final OrganizationRepository organizationRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, OrganizationRepository organizationRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.organizationRepository = organizationRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public CustomerResponse createCustomer(Long organizationId, CreateCustomerRequest request) {
        Organization organization = findActiveOrganization(organizationId);
        validateUniqueEmail(organizationId, request.email(), null);
        validateUniquePhone(organizationId, request.phone(), null);
        Customer customer = customerMapper.toEntity(request);
        customer.setOrganization(organization);
        customer.setActive(true);
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Override @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long organizationId, Long customerId) {
        return customerMapper.toResponse(findCustomer(organizationId, customerId));
    }

    @Override @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers(Long organizationId) {
        findActiveOrganization(organizationId);
        return customerMapper.toResponseList(customerRepository.findByOrganizationIdAndActiveTrue(organizationId));
    }

    @Override @Transactional(readOnly = true)
    public List<CustomerSummaryResponse> getCustomerSummaries(Long organizationId) {
        findActiveOrganization(organizationId);
        return customerMapper.toSummaryResponseList(customerRepository.findByOrganizationIdAndActiveTrue(organizationId));
    }

    @Override
    public CustomerResponse updateCustomer(Long organizationId, Long customerId, UpdateCustomerRequest request) {
        Customer customer = findCustomer(organizationId, customerId);
        validateUniqueEmail(organizationId, request.email(), customer);
        validateUniquePhone(organizationId, request.phone(), customer);
        customerMapper.updateEntityFromRequest(request, customer);
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Override
    public void deactivateCustomer(Long organizationId, Long customerId) {
        Customer customer = findCustomer(organizationId, customerId);
        if (!Boolean.TRUE.equals(customer.getActive())) throw new InvalidResourceStateException("Customer is already inactive");
        customer.setActive(false);
        customerRepository.save(customer);
    }

    @Override @Transactional(readOnly = true)
    public List<CustomerResponse> searchCustomers(Long organizationId, String keyword) {
        findActiveOrganization(organizationId);
        if (keyword == null || keyword.isBlank()) return getAllCustomers(organizationId);
        return customerMapper.toResponseList(customerRepository.findByOrganizationIdAndActiveTrueAndCustomerNameContainingIgnoreCase(organizationId, keyword.trim()));
    }

    @Override
    public CustomerResponse reactivateCustomer(Long organizationId, Long customerId) {
        Customer customer = findCustomer(organizationId, customerId);
        if (Boolean.TRUE.equals(customer.getActive())) throw new InvalidResourceStateException("Customer is already active");
        customer.setActive(true);
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Override @Transactional(readOnly = true)
    public List<CustomerResponse> getInactiveCustomers(Long organizationId) {
        findActiveOrganization(organizationId);
        return customerMapper.toResponseList(customerRepository.findByOrganizationIdAndActiveFalse(organizationId));
    }

    private Customer findCustomer(Long organizationId, Long customerId) {
        findActiveOrganization(organizationId);
        validateId(customerId, "Customer");
        return customerRepository.findByIdAndOrganizationId(customerId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
    }

    private Organization findActiveOrganization(Long organizationId) {
        validateId(organizationId, "Organization");
        return organizationRepository.findByIdAndStatus(organizationId, OrganizationStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active organization", "id", organizationId));
    }

    private void validateUniqueEmail(Long organizationId, String email, Customer existing) {
        if (email == null || email.isBlank()) return;
        String normalized = email.trim().toLowerCase();
        boolean unchanged = existing != null && existing.getEmail() != null && existing.getEmail().equalsIgnoreCase(normalized);
        if (!unchanged && customerRepository.existsByOrganizationIdAndEmailIgnoreCase(organizationId, normalized))
            throw new DuplicateResourceException("Customer", "email", email);
    }

    private void validateUniquePhone(Long organizationId, String phone, Customer existing) {
        if (phone == null || phone.isBlank()) return;
        String normalized = phone.trim();
        boolean unchanged = existing != null && normalized.equals(existing.getPhone());
        if (!unchanged && customerRepository.existsByOrganizationIdAndPhone(organizationId, normalized))
            throw new DuplicateResourceException("Customer", "phone", phone);
    }

    private void validateId(Long id, String resource) {
        if (id == null || id <= 0) throw new InvalidRequestException(resource + " id must be greater than zero");
    }
}
