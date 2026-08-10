package com.ezra_anotida.invoice_maker.service;

import com.ezra_anotida.invoice_maker.dto.payment.*;
import java.util.List;

public interface PaymentService {

    PaymentResponse recordPayment(Long organizationId, Long invoiceId, CreatePaymentRequest request);

    PaymentResponse getPaymentById(Long organizationId, Long paymentId);

    List<PaymentResponse> getAllPayments(Long organizationId);

    List<PaymentResponse> getPaymentsByInvoice(Long organizationId, Long invoiceId);

    PaymentResponse updatePayment(Long organizationId, Long paymentId, UpdatePaymentRequest request);

    void deletePayment(Long organizationId, Long paymentId);
}
