package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.Customer;
import com.ezra_anotida.invoice_maker.entity.Invoice;
import com.ezra_anotida.invoice_maker.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByIdAndOrganizationId(Long invoiceId, Long organizationId);
    Optional<Invoice> findByOrganizationIdAndInvoiceNumber(Long organizationId, String invoiceNumber);
    boolean existsByOrganizationIdAndInvoiceNumber(Long organizationId, String invoiceNumber);
    List<Invoice> findByOrganizationId(Long organizationId);
    List<Invoice> findByOrganizationIdAndCustomer(Long organizationId, Customer customer);
    List<Invoice> findByOrganizationIdAndStatus(Long organizationId, InvoiceStatus status);
    List<Invoice> findByOrganizationIdAndInvoiceDateBetween(Long organizationId, LocalDate startDate, LocalDate endDate);
    List<Invoice> findByOrganizationIdAndDueDateAndStatusNot(Long organizationId, LocalDate date, InvoiceStatus status);
    List<Invoice> findByOrganizationIdAndCustomerAndStatus(Long organizationId, Customer customer, InvoiceStatus status);
}
