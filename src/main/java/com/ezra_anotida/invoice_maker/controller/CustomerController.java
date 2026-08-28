package com.ezra_anotida.invoice_maker.controller;

import com.ezra_anotida.invoice_maker.dto.customer.CreateCustomerRequest;
import com.ezra_anotida.invoice_maker.dto.customer.CustomerResponse;
import com.ezra_anotida.invoice_maker.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/{organizationId}/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @PathVariable Long organizationId,  @RequestBody CreateCustomerRequest createCustomerRequest){

        CustomerResponse customerResponse = customerService.createCustomer(organizationId, createCustomerRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerResponse);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getByCustomerId(@PathVariable Long organizationId, @PathVariable Long customerId){

        CustomerResponse customerResponse = customerService.getCustomerById(organizationId, customerId);

        return ResponseEntity.ok(customerResponse);

    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers(@PathVariable Long organizationId){

        List<CustomerResponse> customers = customerService.getAllCustomers(organizationId);

        return ResponseEntity.ok(customers);
    }
}
