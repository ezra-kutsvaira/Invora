package com.ezra_anotida.invoice_maker.service.impl;

import com.ezra_anotida.invoice_maker.exception.BusinessRuleException;
import com.ezra_anotida.invoice_maker.exception.InvalidRequestException;
import com.ezra_anotida.invoice_maker.exception.InvalidResourceStateException;
import com.ezra_anotida.invoice_maker.exception.ResourceNotFoundException;
import com.ezra_anotida.invoice_maker.dto.invoice.CreateInvoiceRequest;
import com.ezra_anotida.invoice_maker.dto.invoice.InvoiceResponse;
import com.ezra_anotida.invoice_maker.dto.invoice.InvoiceSummaryResponse;
import com.ezra_anotida.invoice_maker.dto.invoice.UpdateInvoiceRequest;
import com.ezra_anotida.invoice_maker.entity.Customer;
import com.ezra_anotida.invoice_maker.entity.Invoice;
import com.ezra_anotida.invoice_maker.entity.InvoiceItem;
import com.ezra_anotida.invoice_maker.enums.InvoiceStatus;
import com.ezra_anotida.invoice_maker.mapper.InvoiceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ezra_anotida.invoice_maker.repository.CustomerRepository;
import com.ezra_anotida.invoice_maker.repository.InvoiceRepository;
import com.ezra_anotida.invoice_maker.service.InvoiceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final CustomerRepository customerRepository;
    private final InvoiceCalculationService invoiceCalculationService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, InvoiceMapper invoiceMapper, CustomerRepository customerRepository, InvoiceCalculationService invoiceCalculationService) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
        this.customerRepository = customerRepository;
        this.invoiceCalculationService = invoiceCalculationService;
    }

    @Override
    public InvoiceResponse createInvoice(CreateInvoiceRequest createInvoiceRequest) {

        validateInvoiceDates(createInvoiceRequest.invoiceDate(), createInvoiceRequest.dueDate());

        Customer customer = findActiveCustomerById(createInvoiceRequest.customerId());

        Invoice invoice = invoiceMapper.toEntity(createInvoiceRequest);

        invoice.setCustomer(customer);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setStatus(InvoiceStatus.DRAFT);

        bindItemsToInvoice(invoice);

        invoiceCalculationService.recalculateInvoiceTotals(invoice);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        return invoiceMapper.toResponse(savedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long invoiceId) {

        Invoice invoice = findInvoiceById(invoiceId);

        return invoiceMapper.toResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceByInvoiceNumber(String invoiceNumber) {
        if (invoiceNumber == null || invoiceNumber.isBlank()) {
            throw new InvalidRequestException("Invoice number cannot be empty");
        }

        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "number", invoiceNumber.trim()));

        return invoiceMapper.toResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoices() {

        List<Invoice> invoices = invoiceRepository.findAll();

        return invoiceMapper.toResponseList(invoices);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceSummaryResponse> getInvoiceSummaries() {

        List<Invoice> invoices = invoiceRepository.findAll();

        return invoiceMapper.toSummaryResponseList(invoices);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByCustomer(Long customerId) {
        Customer customer = findCustomerById(customerId);

        List<Invoice> invoices = invoiceRepository.findByCustomer(customer);

        return invoiceMapper.toResponseList(invoices);
    }

    @Override
    public InvoiceResponse updateInvoice(Long invoiceId, UpdateInvoiceRequest request) {

        Invoice existingInvoice = findInvoiceById(invoiceId);

        validateInvoiceCanBeUpdated(existingInvoice);

        LocalDate invoiceDate = request.invoiceDate() != null ? request.invoiceDate() : existingInvoice.getInvoiceDate();

        LocalDate dueDate = request.dueDate() != null ? request.dueDate() : existingInvoice.getDueDate();

        validateInvoiceDates(invoiceDate, dueDate);

        //updating the customer on the invoice
        if (request.customerId() != null) {
            Customer customer = findActiveCustomerById(request.customerId());

            existingInvoice.setCustomer(customer);
        }

        invoiceMapper.updateEntityRequest(request, existingInvoice);

        bindItemsToInvoice(existingInvoice);

        invoiceCalculationService.recalculateInvoiceTotals(existingInvoice);

        Invoice updatedInvoice = invoiceRepository.save(existingInvoice);

        return invoiceMapper.toResponse(updatedInvoice);
    }

    @Override
    public InvoiceResponse issueInvoice(Long invoiceId) {

        Invoice invoice = findInvoiceById(invoiceId);

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new InvalidResourceStateException("Only draft invoices can be issued");
        }

        if (invoice.getItems() == null || invoice.getItems().isEmpty()) {

            throw new BusinessRuleException("An invoice must contain at least one item");
        }

        invoiceCalculationService.recalculateInvoiceTotals(invoice);

        invoice.setStatus(InvoiceStatus.SENT);

        Invoice issuedInvoice = invoiceRepository.save(invoice);

        return invoiceMapper.toResponse(issuedInvoice);
    }

    @Override
    public InvoiceResponse cancelInvoice(Long invoiceId) {

        Invoice invoice = findInvoiceById(invoiceId);

        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new InvalidResourceStateException("Invoice is already cancelled");
        }

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new InvalidResourceStateException("A paid invoice cannot be cancelled");
        }

        if (invoice.getAmountPaid() != null && invoice.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {

            throw new InvalidResourceStateException("An invoice with recorded payments cannot be cancelled");
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);

        Invoice cancelledInvoice = invoiceRepository.save(invoice);

        return invoiceMapper.toResponse(cancelledInvoice);
    }

    private Customer findActiveCustomerById(Long customerId){
        if(customerId == null || customerId <= 0){
            throw  new InvalidRequestException("Customer id must be greater than zero");
        }

        return customerRepository.findByIdAndActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Active customer", "id" , customerId));
    }

    @Override
    public void deleteInvoice(Long invoiceId) {

        Invoice invoice = findInvoiceById(invoiceId);

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new InvalidResourceStateException("Only draft invoices can be deleted");
        }

        invoiceRepository.delete(invoice);
    }

    private Invoice findInvoiceById(Long invoiceId) {

        if (invoiceId == null) {
            throw new InvalidRequestException("Invoice ID cannot be null");
        }

        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
    }

    private Customer findCustomerById(Long customerId) {

        if (customerId == null) {
            throw new InvalidRequestException("Customer ID cannot be null");
        }

        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
    }

    private String generateInvoiceNumber() {

        String invoiceNumber;

        do {
            invoiceNumber = "INV-"
                    + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();

        } while (invoiceRepository.existsByInvoiceNumber(invoiceNumber));

        return invoiceNumber;
    }

    private void bindItemsToInvoice(Invoice invoice) {

        if (invoice.getItems() == null) {
            return;
        }

        for (InvoiceItem item : invoice.getItems()) {
            item.setInvoice(invoice);
            invoiceCalculationService.calculateLineTotal(item);
        }
    }



    private void validateInvoiceDates(LocalDate invoiceDate, LocalDate dueDate) {
        if (invoiceDate == null) {
            throw new InvalidRequestException("Invoice date is required");
        }

        if (dueDate == null) {
            throw new InvalidRequestException("Due date is required");
        }

        if (dueDate.isBefore(invoiceDate)) {
            throw new InvalidRequestException("Due date cannot be before invoice date");
        }
    }

    private void validateInvoiceCanBeUpdated(Invoice invoice) {

        if(invoice.getStatus() != InvoiceStatus.DRAFT){
            throw new InvalidResourceStateException("Only draft invoice can be updated");
        }
    }


}
