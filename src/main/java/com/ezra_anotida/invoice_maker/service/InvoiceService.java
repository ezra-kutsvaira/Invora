package com.ezra_anotida.invoice_maker.service;

import com.ezra_anotida.invoice_maker.dto.invoice.*;
import java.util.List;

public interface InvoiceService {

    InvoiceResponse createInvoice(Long organizationId, CreateInvoiceRequest request);

    InvoiceResponse getInvoiceById(Long organizationId, Long invoiceId);

    InvoiceResponse getInvoiceByInvoiceNumber(Long organizationId, String invoiceNumber);

    List<InvoiceResponse> getAllInvoices(Long organizationId);

    List<InvoiceSummaryResponse> getInvoiceSummaries(Long organizationId);

    List<InvoiceResponse> getInvoicesByCustomer(Long organizationId, Long customerId);

    InvoiceResponse updateInvoice(Long organizationId, Long invoiceId, UpdateInvoiceRequest request);

    InvoiceResponse issueInvoice(Long organizationId, Long invoiceId);

    InvoiceResponse cancelInvoice(Long organizationId, Long invoiceId);

    void deleteInvoice(Long organizationId, Long invoiceId);
}
