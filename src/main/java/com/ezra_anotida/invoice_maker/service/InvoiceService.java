package com.ezra_anotida.invoice_maker.service;

import com.ezra_anotida.invoice_maker.dto.invoice.CreateInvoiceRequest;
import com.ezra_anotida.invoice_maker.dto.invoice.InvoiceResponse;
import com.ezra_anotida.invoice_maker.dto.invoice.InvoiceSummaryResponse;
import com.ezra_anotida.invoice_maker.dto.invoice.UpdateInvoiceRequest;

import java.util.List;

public interface InvoiceService {

    InvoiceResponse createInvoice(CreateInvoiceRequest createInvoiceRequest);

    InvoiceResponse getInvoiceById(Long invoiceId);

    InvoiceResponse getInvoiceByInvoiceNumber(String invoiceNumber);

    List<InvoiceResponse> getAllInvoices();

    List<InvoiceSummaryResponse> getInvoiceSummaries();

    List<InvoiceResponse> getInvoicesByCustomer(Long customerId);

    InvoiceResponse updateInvoice(Long invoiceId, UpdateInvoiceRequest request);

    InvoiceResponse issueInvoice(Long invoiceId);

    InvoiceResponse cancelInvoice(Long invoiceId);

    void deleteInvoice(Long invoiceId);
}
