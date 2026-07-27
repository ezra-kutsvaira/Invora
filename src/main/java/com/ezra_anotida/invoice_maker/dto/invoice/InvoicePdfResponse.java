package com.ezra_anotida.invoice_maker.dto.invoice;

import com.ezra_anotida.invoice_maker.dto.invoiceitem.InvoiceItemResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoicePdfResponse(
        String invoiceNumber,

        LocalDate invoiceDate,

        LocalDate dueDate,

        String companyName,
        String companyEmail,
        String companyPhone,
        String companyWebsite,
        String companyAddress,
        String companyCity,
        String companyCountry,
        String companyTaxNumber,
        String companyLogoPath,

        String bankName,
        String bankAccountName,
        String bankAccountNumber,
        String bankBranch,
        String bankSwiftCode,

        String customerName,
        String customerEmail,
        String customerPhone,
        String customerAddress,
        String customerCity,
        String customerCountry,
        String customerTaxNumber,

        List<InvoiceItemResponse> items,

        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        BigDecimal amountPaid,
        BigDecimal balanceDue,

        String status,
        String notes,
        String invoiceTerms
) {
}
