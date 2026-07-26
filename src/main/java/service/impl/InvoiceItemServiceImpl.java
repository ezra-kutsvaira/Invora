package service.impl;

import com.ezra_anotida.invoice_maker.exception.InvalidRequestException;
import com.ezra_anotida.invoice_maker.exception.InvalidResourceStateException;
import com.ezra_anotida.invoice_maker.exception.ResourceNotFoundException;
import dto.invoiceitem.CreateInvoiceItemRequest;
import dto.invoiceitem.InvoiceItemResponse;
import dto.invoiceitem.UpdateInvoiceItemRequest;
import entity.Invoice;
import entity.InvoiceItem;
import entity.Product;
import enums.InvoiceStatus;
import mapper.InvoiceItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.InvoiceItemRepository;
import repository.InvoiceRepository;
import repository.ProductRepository;
import service.InvoiceItemService;

import java.util.List;

@Service
@Transactional
public class InvoiceItemServiceImpl implements InvoiceItemService {

    private final InvoiceItemRepository invoiceItemRepository;

    private final InvoiceItemMapper invoiceItemMapper;

    private final InvoiceRepository invoiceRepository;

    private final ProductRepository productRepository;

    private final InvoiceCalculationService invoiceCalculationService;

    public InvoiceItemServiceImpl(InvoiceItemRepository invoiceItemRepository, InvoiceItemMapper invoiceItemMapper, InvoiceRepository invoiceRepository, ProductRepository productRepository , InvoiceCalculationService invoiceCalculationService) {
        this.invoiceItemRepository = invoiceItemRepository;
        this.invoiceItemMapper = invoiceItemMapper;
        this.invoiceRepository = invoiceRepository;
        this.productRepository = productRepository;
        this.invoiceCalculationService = invoiceCalculationService;
    }

    @Override
    public InvoiceItemResponse addItemToInvoice(Long invoiceId, CreateInvoiceItemRequest request) {

        Invoice invoice = findInvoiceById(invoiceId);

        validateInvoiceCanBeModified(invoice);

        InvoiceItem invoiceItem =  invoiceItemMapper.toEntity(request);

        if(request.productId() != null) {
            Product product = findProductById(request.productId());

            invoiceItem.setProduct(product);
        }

        invoice.addItem(invoiceItem);

        //calculate the line Item
        invoiceCalculationService.calculateLineTotal(invoiceItem);

        invoiceCalculationService.recalculateInvoiceTotals(invoice);

        InvoiceItem savedInvoiceItem = invoiceItemRepository.save(invoiceItem);

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

        invoiceCalculationService.calculateLineTotal(existingInvoiceItem);

        invoiceCalculationService.recalculateInvoiceTotals(invoice);

        InvoiceItem updatedInvoiceItem = invoiceItemRepository.save(existingInvoiceItem);
        invoiceRepository.save(invoice);

        return invoiceItemMapper.toResponse(updatedInvoiceItem);
    }

    @Override
    public void removeInvoiceItem(Long invoiceItemId) {

        InvoiceItem invoiceItem = findInvoiceItemById(invoiceItemId);

        Invoice invoice = invoiceItem.getInvoice();

        validateInvoiceCanBeModified(invoice);

        invoice.removeItem(invoiceItem);

        invoiceCalculationService.recalculateInvoiceTotals(invoice);

        invoiceRepository.save(invoice);

    }

    private InvoiceItem findInvoiceItemById(Long invoiceItemId){
        if(invoiceItemId == null){
            throw new InvalidRequestException("Invoice item ID cannot be null");
        }

        return invoiceItemRepository.findById(invoiceItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice item", "id", invoiceItemId));

    }

    private Invoice findInvoiceById(Long invoiceId) {
        if(invoiceId == null){
            throw new InvalidRequestException("Invoice ID cannot be null");
        }

        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
    }

    private Product findProductById(Long productId) {

        if(productId == null){
            throw new InvalidRequestException("Product ID cannot be null");
        }

        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

    }

    private void validateInvoiceCanBeModified(Invoice invoice) {

        if(invoice.getStatus() != InvoiceStatus.DRAFT){
            throw new InvalidResourceStateException("Only draft invoices can be modified");
        }
    }

}
