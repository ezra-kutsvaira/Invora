package com.ezra_anotida.invoice_maker.controller;

import com.ezra_anotida.invoice_maker.dto.invoiceitem.CreateInvoiceItemRequest;
import com.ezra_anotida.invoice_maker.dto.invoiceitem.InvoiceItemResponse;
import com.ezra_anotida.invoice_maker.dto.invoiceitem.UpdateInvoiceItemRequest;
import com.ezra_anotida.invoice_maker.service.InvoiceItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/{organizationId}/invoices/{invoiceId}/items")
public class InvoiceItemController {

    private final InvoiceItemService invoiceItemService;

    public InvoiceItemController(InvoiceItemService invoiceItemService) {
        this.invoiceItemService = invoiceItemService;
    }

    @PostMapping
    public ResponseEntity<InvoiceItemResponse> addItemToItem (@PathVariable ("organizationId") Long organizationId, @PathVariable ("invoiceId") Long invoiceId,@Valid @RequestBody CreateInvoiceItemRequest request){

        InvoiceItemResponse addedItem = invoiceItemService.addItemToInvoice(organizationId, invoiceId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addedItem);
    }

    @GetMapping("/{invoiceItemId}")
     public ResponseEntity<InvoiceItemResponse> getInvoiceItemById(@PathVariable ("organizationId") Long organizationId,@PathVariable ("invoiceItemId") Long invoiceItemId){

        InvoiceItemResponse invoiceItem = invoiceItemService.getInvoiceItemById(organizationId, invoiceItemId);

        return ResponseEntity.ok(invoiceItem);

    }

    @GetMapping
    public ResponseEntity<List<InvoiceItemResponse>> getItemsByInvoice(@PathVariable ("organizationId") Long organizationId, @PathVariable ("invoiceId") Long invoiceId){

        List<InvoiceItemResponse> invoiceItems = invoiceItemService.getItemsByInvoice(organizationId, invoiceId);

        return ResponseEntity.ok(invoiceItems);
    }

    @PutMapping("/{invoiceItemId}")
    public ResponseEntity<InvoiceItemResponse> updateInvoiceItem(@PathVariable ("organizationId") Long organizationId,@PathVariable ("invoiceId") Long invoiceId, @PathVariable ("invoiceItemId") Long invoiceItemId,  @Valid @RequestBody UpdateInvoiceItemRequest request){

        InvoiceItemResponse updatedItem = invoiceItemService.updateInvoiceItem(organizationId, invoiceId, invoiceItemId, request);

        return ResponseEntity.ok(updatedItem);

    }

    @DeleteMapping("/{invoiceItemId}")
    public ResponseEntity<Void> removeInvoiceItem(@PathVariable Long organizationId, @PathVariable Long invoiceId, @PathVariable Long invoiceItemId) {

        invoiceItemService.removeInvoiceItem(organizationId, invoiceId, invoiceItemId);

        return ResponseEntity.noContent().build();
    }
}
