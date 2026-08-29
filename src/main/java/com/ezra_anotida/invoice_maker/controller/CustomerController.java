package com.ezra_anotida.invoice_maker.controller;

import com.ezra_anotida.invoice_maker.dto.customer.CreateCustomerRequest;
import com.ezra_anotida.invoice_maker.dto.customer.CustomerResponse;
import com.ezra_anotida.invoice_maker.dto.customer.CustomerSummaryResponse;
import com.ezra_anotida.invoice_maker.dto.customer.UpdateCustomerRequest;
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
    public ResponseEntity<CustomerResponse> createCustomer(@PathVariable ("organizationId") Long organizationId, @Valid @RequestBody CreateCustomerRequest createCustomerRequest){

        CustomerResponse customer = customerService.createCustomer(organizationId, createCustomerRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customer);
    }


    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getByCustomerId(@PathVariable ("organizationId") Long organizationId, @PathVariable("customerId") Long customerId){

        CustomerResponse customer = customerService.getCustomerById(organizationId, customerId);

        return ResponseEntity.ok(customer);

    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers(@PathVariable ("organizationId") Long organizationId){

        List<CustomerResponse> customers = customerService.getAllCustomers(organizationId);

        return ResponseEntity.ok(customers);
    }

    @GetMapping("/summaries")
    public ResponseEntity<List<CustomerSummaryResponse>> getCustomerSummaries(@PathVariable ("organizationId") Long organizationId){

        List<CustomerSummaryResponse> summaries = customerService.getCustomerSummaries(organizationId);

        return ResponseEntity.ok(summaries);

    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable ("organizationId") Long organizationId, @PathVariable ("customerId") Long customerId, @Valid @RequestBody UpdateCustomerRequest updateCustomerRequest){

        CustomerResponse customer = customerService.updateCustomer(organizationId, customerId, updateCustomerRequest);

        return ResponseEntity.ok(customer);
    }

    @PatchMapping("/{customerId}/deactivate")
    public ResponseEntity<Void> deactivateCustomer(@PathVariable ("organizationId") Long organizationId, @PathVariable ("customerId")  Long customerId){

        customerService.deactivateCustomer(organizationId, customerId);

        return ResponseEntity.noContent().build();
    }

    //Searching Customers
    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponse>> searchCustomers(@PathVariable ("organizationId") Long organizationId, @RequestParam ("keyword") String keyword){

        List<CustomerResponse> customers = customerService.searchCustomers(organizationId, keyword);

        return ResponseEntity.ok(customers);
    }

    //Reactivate Customer
    @PatchMapping("/{customerId}/reactivate")
    public ResponseEntity<CustomerResponse> reactivateCustomer(@PathVariable ("organizationId") Long organizationId, @PathVariable ("customerId") Long customerId){

        CustomerResponse customer = customerService.reactivateCustomer(organizationId, customerId);

        return ResponseEntity.ok(customer);
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<CustomerResponse>> getInactiveCustomers (@PathVariable ("organizationId") Long organizationId){

        List<CustomerResponse> customers = customerService.getInactiveCustomers(organizationId);

        return ResponseEntity.ok(customers);

    }
}
