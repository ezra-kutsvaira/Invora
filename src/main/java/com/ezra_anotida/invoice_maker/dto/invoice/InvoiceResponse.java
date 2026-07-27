package com.ezra_anotida.invoice_maker.dto.invoice;

import com.ezra_anotida.invoice_maker.dto.invoiceitem.InvoiceItemResponse;
import com.ezra_anotida.invoice_maker.dto.payment.PaymentResponse;
import com.ezra_anotida.invoice_maker.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceResponse(
        Long id,

        String invoiceNumber,

        Long customerId,

        String customerName,

        LocalDate invoiceDate,

        LocalDate dueDate,

        BigDecimal subtotal,

        BigDecimal taxAmount,

        BigDecimal discountAmount
        ,
        BigDecimal totalAmount,

        BigDecimal amountPaid,

        BigDecimal balanceDue,

        InvoiceStatus status,

        String notes,

        List<InvoiceItemResponse> items,

        List<PaymentResponse> payments
) {
}