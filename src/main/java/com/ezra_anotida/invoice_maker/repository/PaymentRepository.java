package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.Invoice;
import com.ezra_anotida.invoice_maker.entity.Payment;
import com.ezra_anotida.invoice_maker.enums.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByInvoice (Invoice invoice);

    List<Payment> findPaymentDateBetween (LocalDate startDate , LocalDate endDate);

    List<Payment> findByPaymentMethod (PaymentMethod paymentMethod);

    List<Payment> findByInvoiceId(Long id);
}
