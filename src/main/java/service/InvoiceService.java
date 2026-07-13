package service;

import dto.invoice.CreateInvoiceRequest;
import dto.invoice.InvoiceResponse;
import dto.invoice.InvoiceSummaryResponse;
import dto.invoice.UpdateInvoiceRequest;
import entity.Invoice;

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
