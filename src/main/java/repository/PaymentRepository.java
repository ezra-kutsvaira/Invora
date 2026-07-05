package repository;

import entity.Invoice;
import entity.Payment;
import enums.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByInvoice (Invoice invoice);

    List<Payment> findPaymentDateBetween (LocalDate startDate , LocalDate endDate);

    List<Payment> findByPaymentMethod (PaymentMethod paymentMethod);
}
