package com.ezra_anotida.invoice_maker.repository;


import com.ezra_anotida.invoice_maker.entity.Invoice;
import com.ezra_anotida.invoice_maker.entity.InvoiceItem;
import com.ezra_anotida.invoice_maker.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem,Long> {

    List<InvoiceItem> findByInvoice (Invoice invoice);

    List<InvoiceItem> findByProduct (Product product);
}
