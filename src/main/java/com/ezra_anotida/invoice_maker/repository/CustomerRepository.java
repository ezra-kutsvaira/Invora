package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    List<Customer> findByCustomerNameContainingIgnoreCase(String customerName);

    List<Customer> findByActiveTrue ();

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
