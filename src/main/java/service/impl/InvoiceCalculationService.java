package service.impl;

import com.ezra_anotida.invoice_maker.exception.BusinessRuleException;
import com.ezra_anotida.invoice_maker.exception.InvalidRequestException;
import entity.Invoice;
import entity.InvoiceItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class InvoiceCalculationService {

    public void recalculateInvoiceTotals(Invoice invoice) {
        validateInvoice(invoice);

        BigDecimal subtotal = calculateSubtotal(invoice);

        BigDecimal discountAmount = defaultToZero(invoice.getDiscountAmount());
        BigDecimal taxAmount = defaultToZero(invoice.getTaxAmount());
        BigDecimal amountPaid = defaultToZero(invoice.getAmountPaid());

        validateDiscountAmount(discountAmount, subtotal);

        BigDecimal totalAmount = subtotal
                .subtract(discountAmount)
                .add(taxAmount);

        BigDecimal balanceDue = totalAmount
                .subtract(amountPaid)
                .max(BigDecimal.ZERO);

        invoice.setSubtotal(subtotal);
        invoice.setDiscountAmount(discountAmount);
        invoice.setTaxAmount(taxAmount);
        invoice.setTotalAmount(totalAmount);
        invoice.setAmountPaid(amountPaid);
        invoice.setBalanceDue(balanceDue);
    }

    private BigDecimal calculateSubtotal(Invoice invoice) {
        if (invoice.getItems() == null || invoice.getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return invoice.getItems()
                .stream()
                .map(this::calculateLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateLineTotal(InvoiceItem invoiceItem) {
        validateInvoiceItem(invoiceItem);

        BigDecimal lineTotal = invoiceItem.getUnitPrice()
                .multiply(BigDecimal.valueOf(invoiceItem.getQuantity()));

        invoiceItem.setLineTotal(lineTotal);

        return lineTotal;
    }

    private void validateInvoice(Invoice invoice) {
        if (invoice == null) {
            throw new InvalidRequestException("Invoice cannot be null");
        }
    }

    private void validateInvoiceItem(InvoiceItem invoiceItem) {
        if (invoiceItem == null) {
            throw new InvalidRequestException("Invoice item cannot be null");
        }

        Integer quantity = invoiceItem.getQuantity();
        BigDecimal unitPrice = invoiceItem.getUnitPrice();

        if (quantity == null) {
            throw new InvalidRequestException("Invoice item quantity cannot be null");
        }

        if (quantity <= 0) {
            throw new InvalidRequestException("Invoice item quantity must be greater than zero");
        }

        if (unitPrice == null) {
            throw new InvalidRequestException("Invoice item unit price cannot be null");
        }

        if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRequestException("Invoice item unit price must be greater than zero");
        }
    }

    private void validateDiscountAmount(
            BigDecimal discountAmount,
            BigDecimal subtotal
    ) {
        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRequestException("Discount amount cannot be negative");
        }

        if (discountAmount.compareTo(subtotal) > 0) {
            throw new BusinessRuleException("Discount amount cannot exceed the invoice subtotal");
        }
    }

    private BigDecimal defaultToZero(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
