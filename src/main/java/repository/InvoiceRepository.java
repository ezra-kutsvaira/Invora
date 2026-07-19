package repository;

import entity.Customer;
import entity.Invoice;
import enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    boolean existsByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByCustomer(Customer customer);

    List<Invoice> findByStatus(InvoiceStatus status);

    List<Invoice> findByInvoiceDateBetween(LocalDate startDate, LocalDate endDate);

    List<Invoice> findByDueDateAndStatusNot(LocalDate date, InvoiceStatus status);

    List<Invoice> findByCustomerAndStatus(Customer customer, InvoiceStatus status);

}
