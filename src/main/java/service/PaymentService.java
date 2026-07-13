package service;

import dto.payment.PaymentResponse;
import dto.payment.UpdatePaymentRequest;

import java.util.List;

public interface PaymentService {

    PaymentResponse recordPayment(Long invoiceId, CreatePaymentRequest request);

    PaymentResponse getPaymentById(Long paymentId);

    List<PaymentResponse> getAllPayments();

    List<PaymentResponse> getPaymentsByInvoice(Long invoiceId);

    PaymentResponse updatePayment(Long paymentId, UpdatePaymentRequest request);

    void deletePayment(Long paymentId);
}
