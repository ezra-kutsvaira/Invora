package com.ezra_anotida.invoice_maker.service;

import com.ezra_anotida.invoice_maker.dto.payment.CreatePaymentRequest;
import com.ezra_anotida.invoice_maker.dto.payment.PaymentResponse;
import com.ezra_anotida.invoice_maker.dto.payment.UpdatePaymentRequest;

import java.util.List;

public interface PaymentService {

    PaymentResponse recordPayment(Long invoiceId, CreatePaymentRequest request);

    PaymentResponse getPaymentById(Long paymentId);

    List<PaymentResponse> getAllPayments();

    List<PaymentResponse> getPaymentsByInvoice(Long invoiceId);

    PaymentResponse updatePayment(Long paymentId, UpdatePaymentRequest request);

    void deletePayment(Long paymentId);
}
