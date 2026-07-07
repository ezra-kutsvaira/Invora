package dto.invoice;

import dto.invoiceitem.UpdateInvoiceItemRequest;
import enums.InvoiceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record UpdateInvoiceRequest(
        Long customerId,
        LocalDate invoiceDate,
        LocalDate dueDate,

        @PositiveOrZero(message = "Discount cannot be negative")
        BigDecimal discountAmount,

        InvoiceStatus status,
        String notes,

        @Valid
        List<UpdateInvoiceItemRequest> items
) {
}
