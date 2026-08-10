package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.Customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    Optional<Customer> findByIdAndOrganizationId(Long customerId, Long organizationId);

    Page<Customer> findByOrganizationId(Long organizationId, Pageable pageable);

    boolean existsByOrganizationIdAndEmailIgnoreCase(Long organizationId, String email);

    List<Customer> findByActiveTrueAndCustomerNameContainingIgnoreCase(String customerName);

    List<Customer> findByActiveTrue ();

    List<Customer> findActiveFalse();

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Customer> findByIdAndActiveTrue(Long customerId);

}
