package service.impl;

import dto.invoiceitem.CreateInvoiceItemRequest;
import dto.invoiceitem.InvoiceItemResponse;
import dto.invoiceitem.UpdateInvoiceItemRequest;
import entity.Invoice;
import entity.InvoiceItem;
import entity.Product;
import enums.InvoiceStatus;
import jakarta.persistence.EntityNotFoundException;
import mapper.InvoiceItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.InvoiceItemRepository;
import repository.InvoiceRepository;
import repository.ProductRepository;
import service.InvoiceItemService;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class InvoiceItemServiceImpl implements InvoiceItemService {

    private final InvoiceItemRepository invoiceItemRepository;

    private final InvoiceItemMapper invoiceItemMapper;

    private final InvoiceRepository invoiceRepository;

    private final ProductRepository productRepository;

    public InvoiceItemServiceImpl(InvoiceItemRepository invoiceItemRepository, InvoiceItemMapper invoiceItemMapper, InvoiceRepository invoiceRepository, ProductRepository productRepository) {
        this.invoiceItemRepository = invoiceItemRepository;
        this.invoiceItemMapper = invoiceItemMapper;
        this.invoiceRepository = invoiceRepository;
        this.productRepository = productRepository;
    }

    @Override
    public InvoiceItemResponse addItemToInvoice(Long invoiceId, CreateInvoiceItemRequest request) {

        //Find the invoice
        Invoice invoice = findInvoiceById(invoiceId);

        validateInvoiceCanBeModified(invoice);

        InvoiceItem invoiceItem =  invoiceItemMapper.toEntity(request);

        invoiceItem.setInvoice(invoice);

        if(request.productId() != null) {
            Product product = findProductById(request.productId());

            invoiceItem.setProduct(product);
        }

        //calculate the line Item
        calculateLineTotal(invoiceItem);

        InvoiceItem savedInvoiceItem = invoiceItemRepository.save(invoiceItem);

        recalculateInvoiceTotals(invoice);

        invoiceRepository.save(invoice);

        return  invoiceItemMapper.toResponse(savedInvoiceItem);
    }


    @Override
    @Transactional(readOnly = true)
    public InvoiceItemResponse getInvoiceItemById(Long invoiceItemId) {

        InvoiceItem invoiceItem = findInvoiceItemById(invoiceItemId);

        return invoiceItemMapper.toResponse(invoiceItem);
    }

    @Override
    public List<InvoiceItemResponse> getItemsByInvoice(Long invoiceId) {

        Invoice invoice = findInvoiceById(invoiceId);

        List<InvoiceItem> invoiceItems = invoiceItemRepository.findByInvoice(invoice);

        return invoiceItemMapper.toResponseList(invoiceItems);
    }

    @Override
    public InvoiceItemResponse updateInvoiceItem(Long invoiceItemId, UpdateInvoiceItemRequest request) {

        InvoiceItem existingInvoiceItem = findInvoiceItemById(invoiceItemId);

        Invoice invoice = existingInvoiceItem.getInvoice();

        validateInvoiceCanBeModified(invoice);

        if(request.productId() != null){
            Product product = findProductById(request.productId());

            existingInvoiceItem.setProduct(product);
        }

        invoiceItemMapper.updateEntityFromRequest(request, existingInvoiceItem);

        calculateLineTotal(existingInvoiceItem);

        InvoiceItem updatedInvoiceItem = invoiceItemRepository.save(existingInvoiceItem);

        recalculateInvoiceTotals(invoice);

        invoiceRepository.save(invoice);

        return invoiceItemMapper.toResponse(updatedInvoiceItem);
    }

    @Override
    public void removeInvoiceItem(Long invoiceItemId) {

        InvoiceItem invoiceItem = findInvoiceItemById(invoiceItemId);

        Invoice invoice = invoiceItem.getInvoice();

        validateInvoiceCanBeModified(invoice);

        invoiceItemRepository.delete(invoiceItem);

        invoice.getItems().remove(invoiceItem);

        recalculateInvoiceTotals(invoice);

        invoiceRepository.save(invoice);

    }

    private InvoiceItem findInvoiceItemById(Long invoiceItemId){
        if(invoiceItemId == null){
            throw new IllegalArgumentException("Invoice Item ID cannot be null");
        }

        return invoiceItemRepository.findById(invoiceItemId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice item with ID" + invoiceItemId + " not found"));

    }

    private Invoice findInvoiceById(Long invoiceId) {
        if(invoiceId == null){
            throw new IllegalArgumentException("Invoice ID cannot be null");
        }

        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice with ID" + invoiceId + " not found"));
    }

    private Product findProductById(Long productId) {

        if(productId == null){
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID" + productId + " not found"));

    }

    private void validateInvoiceCanBeModified(Invoice invoice) {

        if(invoice.getStatus() != InvoiceStatus.DRAFT){
            throw new IllegalArgumentException("Invoice can only be modified only if the Invoice is in DRAFT Status");
        }
    }

    private void calculateLineTotal(InvoiceItem invoiceItem){

        if(invoiceItem.getQuantity() == null){
            throw new IllegalArgumentException("Invoice quantity cannot be null");
        }

        if (invoiceItem.getUnitPrice() == null) {
            throw new IllegalArgumentException("Invoice item unit price is required"
            );
        }

        if (invoiceItem.getQuantity() <= 0) {
            throw new IllegalArgumentException("Invoice item quantity must be greater than zero"
            );
        }

        if (invoiceItem.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invoice item unit price must be greater than zero"
            );
        }

        BigDecimal lineTotal = invoiceItem.getUnitPrice()
                .multiply(BigDecimal.valueOf(invoiceItem.getQuantity()));

        invoiceItem.setLineTotal(lineTotal);
    }


    private void recalculateInvoiceTotals(Invoice invoice) {

        BigDecimal subtotal = invoice.getItems()
                .stream()
                .map(item -> {calculateLineTotal(item);
                    return item.getLineTotal();
                })
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        BigDecimal discountAmount = defaultToZero(invoice.getDiscountAmount());

        BigDecimal taxAmount = defaultToZero(invoice.getTaxAmount());

        BigDecimal amountPaid = defaultToZero(invoice.getAmountPaid());

        if (discountAmount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException("Discount amount cannot exceed the invoice subtotal");
        }

        BigDecimal totalAmount = subtotal
                .subtract(discountAmount)
                .add(taxAmount);

        BigDecimal balanceDue =
                totalAmount.subtract(amountPaid);

        if (balanceDue.compareTo(BigDecimal.ZERO) < 0) {
            balanceDue = BigDecimal.ZERO;
        }

        invoice.setSubtotal(subtotal);
        invoice.setDiscountAmount(discountAmount);
        invoice.setTaxAmount(taxAmount);
        invoice.setTotalAmount(totalAmount);
        invoice.setAmountPaid(amountPaid);
        invoice.setBalanceDue(balanceDue);
    }

    private BigDecimal defaultToZero(
            BigDecimal amount
    ) {
        return amount == null
                ? BigDecimal.ZERO
                : amount;
    }

}
