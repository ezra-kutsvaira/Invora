package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByIdAndOrganizationId(Long customerId, Long organizationId);

    Optional<Customer> findByIdAndOrganizationIdAndActiveTrue(Long customerId, Long organizationId);

    List<Customer> findByOrganizationIdAndActiveTrue(Long organizationId);

    List<Customer> findByOrganizationIdAndActiveFalse(Long organizationId);

    List<Customer> findByOrganizationIdAndActiveTrueAndCustomerNameContainingIgnoreCase(Long organizationId, String customerName);

    boolean existsByOrganizationIdAndEmailIgnoreCase(Long organizationId, String email);

    boolean existsByOrganizationIdAndPhone(Long organizationId, String phone);
}
