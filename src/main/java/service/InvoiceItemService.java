package service;

import dto.invoiceitem.CreateInvoiceItemRequest;
import dto.invoiceitem.InvoiceItemResponse;
import dto.invoiceitem.UpdateInvoiceItemRequest;

import java.util.List;

public interface InvoiceItemService {

    InvoiceItemResponse addItemToInvoice(Long invoiceId, CreateInvoiceItemRequest request);

    InvoiceItemResponse getInvoiceItemById(Long invoiceItemId);

    List<InvoiceItemResponse> getItemsByInvoice(Long invoiceId);

    InvoiceItemResponse updateInvoiceItem(Long invoiceItemId, UpdateInvoiceItemRequest request);

    void removeInvoiceItem(Long invoiceItemId);
}
