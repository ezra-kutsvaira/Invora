package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    Optional<InvoiceItem> findByIdAndInvoice_Organization_Id(Long itemId, Long organizationId);

    List<InvoiceItem> findByInvoice_Organization_IdAndInvoice_Id(Long organizationId, Long invoiceId);
}
