package service.impl;

import dto.invoice.CreateInvoiceRequest;
import dto.invoice.InvoiceResponse;
import dto.invoice.InvoiceSummaryResponse;
import dto.invoice.UpdateInvoiceRequest;
import entity.Customer;
import entity.Invoice;
import entity.InvoiceItem;
import enums.InvoiceStatus;
import jakarta.persistence.EntityNotFoundException;
import mapper.InvoiceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.CustomerRepository;
import repository.InvoiceRepository;
import service.InvoiceService;

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

        Customer customer = findCustomerById(createInvoiceRequest.customerId());

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
            throw new IllegalArgumentException("Invoice number cannot be empty");
        }

        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber.trim())
                .orElseThrow(() -> new EntityNotFoundException("Invoice with number " + invoiceNumber + " was not found"));

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
            Customer customer = findCustomerById(request.customerId());

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
            throw new IllegalStateException("Only draft invoices can be issued");
        }

        if (invoice.getItems() == null || invoice.getItems().isEmpty()) {

            throw new IllegalStateException("An invoice must contain at least one item");
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
            throw new IllegalStateException("Invoice is already cancelled");
        }

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("A paid invoice cannot be cancelled"
            );
        }

        if (invoice.getAmountPaid() != null && invoice.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {

            throw new IllegalStateException("An invoice with recorded payments cannot be cancelled");
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);

        Invoice cancelledInvoice = invoiceRepository.save(invoice);

        return invoiceMapper.toResponse(cancelledInvoice);
    }

    @Override
    public void deleteInvoice(Long invoiceId) {

        Invoice invoice = findInvoiceById(invoiceId);

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Only draft invoices can be deleted");
        }

        invoiceRepository.delete(invoice);
    }

    private Invoice findInvoiceById(Long invoiceId) {

        if (invoiceId == null) {
            throw new IllegalArgumentException("Invoice ID cannot be null");
        }

        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice with ID " + invoiceId + " was not found"));
    }

    private Customer findCustomerById(Long customerId) {

        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }

        return customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer with ID " + customerId + " was not found"));
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
            throw new IllegalArgumentException("Invoice date is required");
        }

        if (dueDate == null) {throw new IllegalArgumentException(
                    "Due date is required"
            );
        }

        if (dueDate.isBefore(invoiceDate)) {throw new IllegalArgumentException(
                    "Due date cannot be before invoice date"
            );
        }
    }

    private void validateInvoiceCanBeUpdated(Invoice invoice) {
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException(
                    "A cancelled invoice cannot be updated"
            );
        }

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("A paid invoice cannot be updated");
        }
    }
}