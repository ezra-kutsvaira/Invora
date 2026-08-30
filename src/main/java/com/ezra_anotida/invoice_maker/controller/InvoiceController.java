package com.ezra_anotida.invoice_maker.controller;


import com.ezra_anotida.invoice_maker.dto.invoice.CreateInvoiceRequest;
import com.ezra_anotida.invoice_maker.dto.invoice.InvoiceResponse;
import com.ezra_anotida.invoice_maker.dto.invoice.InvoiceSummaryResponse;
import com.ezra_anotida.invoice_maker.dto.invoice.UpdateInvoiceRequest;
import com.ezra_anotida.invoice_maker.service.InvoiceService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/{organizationId}/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice (@PathVariable ("organizationId") Long organizationId, @Valid @RequestBody CreateInvoiceRequest request){

        InvoiceResponse invoice = invoiceService.createInvoice(organizationId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invoice);
    }


    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponse> getInvoiceById (@PathVariable ("organizationId") Long organizationId, @PathVariable ("invoiceId") Long invoiceId){

        InvoiceResponse invoice = invoiceService.getInvoiceById(organizationId, invoiceId);

        return ResponseEntity.ok(invoice);
    }

    //Get By InvoiceNumber
    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<InvoiceResponse> getInvoiceByInvoiceNumber (@PathVariable ("organizationId") Long organizationId, @PathVariable ("invoiceNumber") String invoiceNumber){

        InvoiceResponse invoiceByInvoiceNumber = invoiceService.getInvoiceByInvoiceNumber(organizationId, invoiceNumber);

        return ResponseEntity.ok(invoiceByInvoiceNumber);
    }

    //Get All Invoices
    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices (@PathVariable ("organizationId") Long organizationId){

        List<InvoiceResponse> invoices = invoiceService.getAllInvoices(organizationId);

        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/summaries")
    public ResponseEntity<List<InvoiceSummaryResponse>> getInvoiceSummaries (@PathVariable ("organizationId") Long organizationId) {

        List<InvoiceSummaryResponse> summaryResponses = invoiceService.getInvoiceSummaries(organizationId);

        return ResponseEntity.ok(summaryResponses);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByCustomer (@PathVariable ("organizationId") Long organizationId, @PathVariable ("customerId") Long customerId){

        List<InvoiceResponse> invoices = invoiceService.getInvoicesByCustomer(organizationId, customerId);

        return ResponseEntity.ok(invoices);
    }

    @PutMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponse> updateInvoice (@PathVariable ("organizationId") Long organizationId, @PathVariable ("invoiceId") Long invoiceId, @Valid @RequestBody UpdateInvoiceRequest updateInvoiceRequest){

        InvoiceResponse updatedInvoice = invoiceService.updateInvoice(organizationId, invoiceId, updateInvoiceRequest);

        return  ResponseEntity.ok(updatedInvoice);

    }

    @PatchMapping("/{invoiceId}/issue")
    public ResponseEntity<InvoiceResponse> issueInvoice (@PathVariable ("organizationId") Long organizationId, @PathVariable ("invoiceId") Long invoiceId){

        InvoiceResponse invoice = invoiceService.issueInvoice(organizationId, invoiceId);

        return ResponseEntity.ok(invoice);

    }

    @PatchMapping("/{invoiceId}/cancel")
    public ResponseEntity<InvoiceResponse> cancelInvoice (@PathVariable ("organizationId") Long organizationId, @PathVariable ("invoiceId") Long invoiceId){

        InvoiceResponse invoice = invoiceService.cancelInvoice(organizationId, invoiceId);

        return ResponseEntity.ok(invoice);
    }


    @DeleteMapping("/{invoiceId}/delete")
    public ResponseEntity<Void> deleteInvoice (@PathVariable ("organizationId") Long organizationId, @PathVariable ("invoiceId") Long invoiceId){

        invoiceService.deleteInvoice(organizationId, invoiceId);

        return ResponseEntity.noContent().build();
    }

}
