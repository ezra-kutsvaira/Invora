package repository;

import entity.Payment;
import entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    Optional<Receipt> findByReceiptNumber (String receiptNumber);

    boolean existsByReceiptNumber (String receiptNumber);

    Optional<Receipt> findByPayment (Payment payment);
}
