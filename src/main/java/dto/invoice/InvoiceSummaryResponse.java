package dto.invoice;

import enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceSummaryResponse(
        Long id,

        String invoiceNumber,

        String customerName,

        LocalDate invoiceDate,

        LocalDate dueDate,

        BigDecimal totalAmount,

        BigDecimal balanceDue,

        InvoiceStatus status
) {
}