package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByIdAndInvoice_Organization_Id(Long paymentId, Long organizationId);

    List<Payment> findByInvoice_Organization_Id(Long organizationId);

    List<Payment> findByInvoice_Organization_IdAndInvoice_Id(Long organizationId, Long invoiceId);

    List<Payment> findByInvoice_Id(Long invoiceId);
}
