package com.ezra_anotida.invoice_maker.service;

import com.ezra_anotida.invoice_maker.dto.invoiceitem.CreateInvoiceItemRequest;
import com.ezra_anotida.invoice_maker.dto.invoiceitem.InvoiceItemResponse;
import com.ezra_anotida.invoice_maker.dto.invoiceitem.UpdateInvoiceItemRequest;

import java.util.List;

public interface InvoiceItemService {

    InvoiceItemResponse addItemToInvoice(Long invoiceId, CreateInvoiceItemRequest request);

    InvoiceItemResponse getInvoiceItemById(Long invoiceItemId);

    List<InvoiceItemResponse> getItemsByInvoice(Long invoiceId);

    InvoiceItemResponse updateInvoiceItem(Long invoiceItemId, UpdateInvoiceItemRequest request);

    void removeInvoiceItem(Long invoiceItemId);
}
