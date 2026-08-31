package com.ezra_anotida.invoice_maker.service;

import com.ezra_anotida.invoice_maker.dto.invoiceitem.*;
import java.util.List;

public interface InvoiceItemService {
    InvoiceItemResponse addItemToInvoice(Long organizationId, Long invoiceId, CreateInvoiceItemRequest request);

    InvoiceItemResponse getInvoiceItemById(Long organizationId, Long invoiceItemId);

    List<InvoiceItemResponse> getItemsByInvoice(Long organizationId, Long invoiceId);

    InvoiceItemResponse updateInvoiceItem(Long organizationId, Long invoiceId,  Long invoiceItemId, UpdateInvoiceItemRequest request);

    void removeInvoiceItem(Long organizationId,Long invoiceId, Long invoiceItemId);
}
