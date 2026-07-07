package dto.invoice;

import dto.invoiceitem.InvoiceItemResponse;
import dto.payment.PaymentResponse;
import enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceDetailsResponse(
        Long id,
        String invoiceNumber,

        Long customerId,

        String customerName,

        String customerEmail,

        String customerPhone,

        String customerAddress,

        String customerCity,

        String customerCountry,

        String customerTaxNumber,

        LocalDate invoiceDate,

        LocalDate dueDate,

        BigDecimal subtotal,

        BigDecimal taxAmount,

        BigDecimal discountAmount,

        BigDecimal totalAmount,

        BigDecimal amountPaid,

        BigDecimal balanceDue,

        InvoiceStatus status,

        String notes,

        List<InvoiceItemResponse> items,

        List<PaymentResponse> payments
) {
}
