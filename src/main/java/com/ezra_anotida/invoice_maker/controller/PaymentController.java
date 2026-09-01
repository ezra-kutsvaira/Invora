package com.ezra_anotida.invoice_maker.controller;

import com.ezra_anotida.invoice_maker.dto.payment.CreatePaymentRequest;
import com.ezra_anotida.invoice_maker.dto.payment.PaymentResponse;
import com.ezra_anotida.invoice_maker.dto.payment.UpdatePaymentRequest;
import com.ezra_anotida.invoice_maker.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/{organizationId}/payments")
public class PaymentController {

    private final PaymentService paymentService;


    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/invoice/{invoiceId}")
    public ResponseEntity<PaymentResponse> recordPayment (@PathVariable ("organizationId") Long organizationId, @PathVariable ("invoiceId") Long invoiceId, @Valid @RequestBody CreatePaymentRequest createPaymentRequest){

        PaymentResponse payment = paymentService.recordPayment(organizationId, invoiceId, createPaymentRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(payment);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById (@PathVariable ("organizationId") Long organizationId, @PathVariable ("paymentId") Long paymentId){

        PaymentResponse payment = paymentService.getPaymentById(organizationId, paymentId);

        return ResponseEntity.ok(payment);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments (@PathVariable ("organizationId") Long organizationId){

       List<PaymentResponse> payments = paymentService.getAllPayments(organizationId);

       return ResponseEntity.ok(payments);
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByInvoice (@PathVariable ("organizationId") Long organizationId, @PathVariable ("invoiceId") Long invoiceId){

        List<PaymentResponse> paymentsByInvoice = paymentService.getPaymentsByInvoice(organizationId, invoiceId);

        return ResponseEntity.ok(paymentsByInvoice);
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> updatePayment(@PathVariable ("organizationId") Long organizationId, @PathVariable ("paymentId") Long paymentId, @Valid @RequestBody UpdatePaymentRequest request){

       PaymentResponse updatedPayment = paymentService.updatePayment(organizationId, paymentId, request);

        return ResponseEntity.ok(updatedPayment);

    }

    @PatchMapping("/{paymentId}")
    public ResponseEntity<Void> deletePayment (@PathVariable ("organizationId") Long organizationId, @PathVariable ("paymentId") Long paymentId){

        paymentService.deletePayment(organizationId, paymentId);

        return ResponseEntity.noContent().build();
    }
}
