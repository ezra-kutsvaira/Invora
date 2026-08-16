package com.ezra_anotida.invoice_maker.service.impl;

import com.ezra_anotida.invoice_maker.dto.invoiceitem.*;
import com.ezra_anotida.invoice_maker.entity.*;
import com.ezra_anotida.invoice_maker.enums.InvoiceStatus;
import com.ezra_anotida.invoice_maker.mapper.InvoiceItemMapper;
import com.ezra_anotida.invoice_maker.exception.InvalidRequestException;
import com.ezra_anotida.invoice_maker.exception.InvalidResourceStateException;
import com.ezra_anotida.invoice_maker.exception.ResourceNotFoundException;
import com.ezra_anotida.invoice_maker.repository.*;
import com.ezra_anotida.invoice_maker.service.InvoiceItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class InvoiceItemServiceImpl implements InvoiceItemService {

    private final InvoiceItemRepository itemRepository;

    private final InvoiceRepository invoiceRepository;

    private final ProductRepository productRepository;

    private final InvoiceItemMapper mapper;

    private final InvoiceCalculationService calculationService;

    public InvoiceItemServiceImpl(InvoiceItemRepository itemRepository, InvoiceRepository invoiceRepository, ProductRepository productRepository, InvoiceItemMapper mapper, InvoiceCalculationService calculationService) {
        this.itemRepository = itemRepository;
        this.invoiceRepository = invoiceRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
        this.calculationService = calculationService;
    }

    @Override
    public InvoiceItemResponse addItemToInvoice(Long organizationId, Long invoiceId, CreateInvoiceItemRequest request) {

        Invoice invoice = findInvoice(organizationId, invoiceId);

        requireDraft(invoice);

        InvoiceItem item = mapper.toEntity(request);

        if (request.productId() != null){
            item.setProduct(findProduct(organizationId, request.productId()));
        }

        invoice.addItem(item);

        calculationService.calculateLineTotal(item);

        calculationService.recalculateInvoiceTotals(invoice);

        InvoiceItem saved = itemRepository.save(item);

        invoiceRepository.save(invoice);

        return mapper.toResponse(saved);
    }

    @Override @Transactional(readOnly = true)
    public InvoiceItemResponse getInvoiceItemById(Long organizationId, Long itemId) {

        return mapper.toResponse(findItem(organizationId, itemId));

    }

    @Override @Transactional(readOnly = true)
    public List<InvoiceItemResponse> getItemsByInvoice(Long organizationId, Long invoiceId) {

        findInvoice(organizationId, invoiceId);

        return mapper.toResponseList(itemRepository.findByInvoice_Organization_IdAndInvoice_Id(organizationId, invoiceId));
    }

    @Override
    public InvoiceItemResponse updateInvoiceItem(Long organizationId, Long itemId, UpdateInvoiceItemRequest request) {

        InvoiceItem item = findItem(organizationId, itemId);

        Invoice invoice = item.getInvoice();

        requireDraft(invoice);

        if (request.productId() != null){
            item.setProduct(findProduct(organizationId, request.productId()));
        }

        mapper.updateEntityFromRequest(request, item);

        calculationService.calculateLineTotal(item);

        calculationService.recalculateInvoiceTotals(invoice);

        InvoiceItem saved = itemRepository.save(item);

        invoiceRepository.save(invoice);

        return mapper.toResponse(saved);
    }

    @Override
    public void removeInvoiceItem(Long organizationId, Long itemId) {

        InvoiceItem item = findItem(organizationId, itemId);

        Invoice invoice = item.getInvoice();

        requireDraft(invoice);

        invoice.removeItem(item);

        calculationService.recalculateInvoiceTotals(invoice);

        invoiceRepository.save(invoice);
    }

    private InvoiceItem findItem(Long organizationId, Long itemId) {

        validateId(itemId, "Invoice item");

        return itemRepository.findByIdAndInvoice_Organization_Id(itemId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice item", "id", itemId));
    }

    private Invoice findInvoice(Long organizationId, Long invoiceId) {

        validateId(invoiceId, "Invoice");

        return invoiceRepository.findByIdAndOrganizationId(invoiceId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
    }

    private Product findProduct(Long organizationId, Long productId) {

        validateId(productId, "Product");

        return productRepository.findByIdAndOrganizationIdAndActiveTrue(productId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Active product", "id", productId));
    }

    private void requireDraft(Invoice invoice) {
        if (invoice.getStatus() != InvoiceStatus.DRAFT)
            throw new InvalidResourceStateException("Only draft invoices can be modified");
    }

    private void validateId(Long id, String resource) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException(resource + " id must be greater than zero");
        }
    }
}
