package com.ezra_anotida.invoice_maker.service.impl;

import com.ezra_anotida.invoice_maker.dto.invoice.*;
import com.ezra_anotida.invoice_maker.entity.*;
import com.ezra_anotida.invoice_maker.enums.InvoiceStatus;
import com.ezra_anotida.invoice_maker.enums.OrganizationStatus;
import com.ezra_anotida.invoice_maker.exception.*;
import com.ezra_anotida.invoice_maker.mapper.InvoiceMapper;
import com.ezra_anotida.invoice_maker.repository.*;
import com.ezra_anotida.invoice_maker.service.InvoiceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final OrganizationRepository organizationRepository;
    private final InvoiceMapper invoiceMapper;
    private final InvoiceCalculationService calculationService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, CustomerRepository customerRepository,
                              OrganizationRepository organizationRepository, InvoiceMapper invoiceMapper,
                              InvoiceCalculationService calculationService) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.organizationRepository = organizationRepository;
        this.invoiceMapper = invoiceMapper;
        this.calculationService = calculationService;
    }

    @Override
    public InvoiceResponse createInvoice(Long organizationId, CreateInvoiceRequest request) {
        Organization organization = findActiveOrganization(organizationId);
        validateDates(request.invoiceDate(), request.dueDate());
        Customer customer = findActiveCustomer(organizationId, request.customerId());
        Invoice invoice = invoiceMapper.toEntity(request);
        invoice.setOrganization(organization);
        invoice.setCustomer(customer);
        invoice.setInvoiceNumber(generateInvoiceNumber(organizationId));
        invoice.setStatus(InvoiceStatus.DRAFT);
        bindItems(invoice);
        calculationService.recalculateInvoiceTotals(invoice);
        return invoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    @Override @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long organizationId, Long invoiceId) {
        return invoiceMapper.toResponse(findInvoice(organizationId, invoiceId));
    }

    @Override @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceByInvoiceNumber(Long organizationId, String invoiceNumber) {
        findActiveOrganization(organizationId);
        if (invoiceNumber == null || invoiceNumber.isBlank()) throw new InvalidRequestException("Invoice number cannot be empty");
        String normalized = invoiceNumber.trim();
        return invoiceMapper.toResponse(invoiceRepository.findByOrganizationIdAndInvoiceNumber(organizationId, normalized)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "number", normalized)));
    }

    @Override @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoices(Long organizationId) {
        findActiveOrganization(organizationId);
        return invoiceMapper.toResponseList(invoiceRepository.findByOrganizationId(organizationId));
    }

    @Override @Transactional(readOnly = true)
    public List<InvoiceSummaryResponse> getInvoiceSummaries(Long organizationId) {
        findActiveOrganization(organizationId);
        return invoiceMapper.toSummaryResponseList(invoiceRepository.findByOrganizationId(organizationId));
    }

    @Override @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByCustomer(Long organizationId, Long customerId) {
        Customer customer = findCustomer(organizationId, customerId);
        return invoiceMapper.toResponseList(invoiceRepository.findByOrganizationIdAndCustomer(organizationId, customer));
    }

    @Override
    public InvoiceResponse updateInvoice(Long organizationId, Long invoiceId, UpdateInvoiceRequest request) {
        Invoice invoice = findInvoice(organizationId, invoiceId);
        requireDraft(invoice);
        LocalDate invoiceDate = request.invoiceDate() != null ? request.invoiceDate() : invoice.getInvoiceDate();
        LocalDate dueDate = request.dueDate() != null ? request.dueDate() : invoice.getDueDate();
        validateDates(invoiceDate, dueDate);
        if (request.customerId() != null) invoice.setCustomer(findActiveCustomer(organizationId, request.customerId()));
        invoiceMapper.updateEntityRequest(request, invoice);
        bindItems(invoice);
        calculationService.recalculateInvoiceTotals(invoice);
        return invoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceResponse issueInvoice(Long organizationId, Long invoiceId) {
        Invoice invoice = findInvoice(organizationId, invoiceId);
        requireDraft(invoice);
        if (invoice.getItems() == null || invoice.getItems().isEmpty())
            throw new BusinessRuleException("An invoice must contain at least one item");
        calculationService.recalculateInvoiceTotals(invoice);
        invoice.setStatus(InvoiceStatus.SENT);
        return invoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceResponse cancelInvoice(Long organizationId, Long invoiceId) {
        Invoice invoice = findInvoice(organizationId, invoiceId);
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) throw new InvalidResourceStateException("Invoice is already cancelled");
        if (invoice.getStatus() == InvoiceStatus.PAID) throw new InvalidResourceStateException("A paid invoice cannot be cancelled");
        if (invoice.getAmountPaid() != null && invoice.getAmountPaid().compareTo(BigDecimal.ZERO) > 0)
            throw new InvalidResourceStateException("An invoice with recorded payments cannot be cancelled");
        invoice.setStatus(InvoiceStatus.CANCELLED);
        return invoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    @Override
    public void deleteInvoice(Long organizationId, Long invoiceId) {
        Invoice invoice = findInvoice(organizationId, invoiceId);
        requireDraft(invoice);
        invoiceRepository.delete(invoice);
    }

    private Invoice findInvoice(Long organizationId, Long invoiceId) {
        findActiveOrganization(organizationId);
        validateId(invoiceId, "Invoice");
        return invoiceRepository.findByIdAndOrganizationId(invoiceId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
    }

    private Customer findCustomer(Long organizationId, Long customerId) {
        validateId(customerId, "Customer");
        return customerRepository.findByIdAndOrganizationId(customerId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
    }

    private Customer findActiveCustomer(Long organizationId, Long customerId) {
        validateId(customerId, "Customer");
        return customerRepository.findByIdAndOrganizationIdAndActiveTrue(customerId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Active customer", "id", customerId));
    }

    private Organization findActiveOrganization(Long organizationId) {
        validateId(organizationId, "Organization");
        return organizationRepository.findByIdAndStatus(organizationId, OrganizationStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active organization", "id", organizationId));
    }

    private String generateInvoiceNumber(Long organizationId) {
        String number;
        do {
            number = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (invoiceRepository.existsByOrganizationIdAndInvoiceNumber(organizationId, number));
        return number;
    }

    private void bindItems(Invoice invoice) {
        if (invoice.getItems() == null) return;
        for (InvoiceItem item : invoice.getItems()) {
            item.setInvoice(invoice);
            calculationService.calculateLineTotal(item);
        }
    }

    private void validateDates(LocalDate invoiceDate, LocalDate dueDate) {
        if (invoiceDate == null || dueDate == null) throw new InvalidRequestException("Invoice date and due date are required");
        if (dueDate.isBefore(invoiceDate)) throw new InvalidRequestException("Due date cannot be before invoice date");
    }

    private void requireDraft(Invoice invoice) {
        if (invoice.getStatus() != InvoiceStatus.DRAFT)
            throw new InvalidResourceStateException("Only draft invoices can be modified");
    }

    private void validateId(Long id, String resource) {
        if (id == null || id <= 0) throw new InvalidRequestException(resource + " id must be greater than zero");
    }
}
