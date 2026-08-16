package com.ezra_anotida.invoice_maker.service.impl;

import com.ezra_anotida.invoice_maker.dto.payment.*;
import com.ezra_anotida.invoice_maker.entity.*;
import com.ezra_anotida.invoice_maker.enums.InvoiceStatus;
import com.ezra_anotida.invoice_maker.exception.*;
import com.ezra_anotida.invoice_maker.mapper.PaymentMapper;
import com.ezra_anotida.invoice_maker.mapper.exception.BusinessRuleException;
import com.ezra_anotida.invoice_maker.mapper.exception.InvalidRequestException;
import com.ezra_anotida.invoice_maker.mapper.exception.InvalidResourceStateException;
import com.ezra_anotida.invoice_maker.mapper.exception.ResourceNotFoundException;
import com.ezra_anotida.invoice_maker.repository.*;
import com.ezra_anotida.invoice_maker.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentMapper mapper;
    private final InvoiceCalculationService calculationService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, InvoiceRepository invoiceRepository, PaymentMapper mapper, InvoiceCalculationService calculationService) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.mapper = mapper;
        this.calculationService = calculationService;
    }

    @Override
    public PaymentResponse recordPayment(Long organizationId, Long invoiceId, CreatePaymentRequest request) {

        Invoice invoice = findInvoice(organizationId, invoiceId);

        validateInvoiceCanReceivePayment(invoice);

        validateAmount(request.amount());

        validateNotOverBalance(request.amount(), invoice.getBalanceDue());

        Payment payment = mapper.toEntity(request);

        payment.setInvoice(invoice);

        Payment saved = paymentRepository.saveAndFlush(payment);

        recalculate(invoice);

        updateStatus(invoice);

        return mapper.toResponse(saved);
    }

    @Override @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long organizationId, Long paymentId) {

        return mapper.toResponse(findPayment(organizationId, paymentId));
    }

    @Override @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments(Long organizationId) {

        return mapper.toResponseList(paymentRepository.findByInvoice_Organization_Id(organizationId));
    }

    @Override @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByInvoice(Long organizationId, Long invoiceId) {

        findInvoice(organizationId, invoiceId);

        return mapper.toResponseList(paymentRepository.findByInvoice_Organization_IdAndInvoice_Id(organizationId, invoiceId));
    }

    @Override
    public PaymentResponse updatePayment(Long organizationId, Long paymentId, UpdatePaymentRequest request) {

        Payment payment = findPayment(organizationId, paymentId);

        Invoice invoice = payment.getInvoice();

        BigDecimal previousAmount = payment.getAmount();

        if (request.amount() != null) {

            validateAmount(request.amount());

            BigDecimal available = invoice.getBalanceDue().add(previousAmount);

            validateNotOverBalance(request.amount(), available);
        }

        mapper.updateEntityFromRequest(request, payment);

        Payment saved = paymentRepository.saveAndFlush(payment);

        recalculate(invoice);

        updateStatus(invoice);

        return mapper.toResponse(saved);
    }

    @Override
    public void deletePayment(Long organizationId, Long paymentId) {

        Payment payment = findPayment(organizationId, paymentId);

        Invoice invoice = payment.getInvoice();

        paymentRepository.delete(payment);

        paymentRepository.flush();

        recalculate(invoice);

        updateStatus(invoice);
    }

    private Payment findPayment(Long organizationId, Long paymentId) {

        validateId(paymentId, "Payment");

        return paymentRepository.findByIdAndInvoice_Organization_Id(paymentId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
    }

    private Invoice findInvoice(Long organizationId, Long invoiceId) {
        validateId(invoiceId, "Invoice");

        return invoiceRepository.findByIdAndOrganizationId(invoiceId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
    }

    private void recalculate(Invoice invoice) {
        BigDecimal paid = paymentRepository.findByInvoice_Id(invoice.getId())
                .stream()
                .map(Payment::getAmount)
                .filter(a -> a != null).reduce(BigDecimal.ZERO, BigDecimal::add);

        invoice.setAmountPaid(paid);

        calculationService.recalculateInvoiceTotals(invoice);

        invoiceRepository.save(invoice);
    }

    private void validateInvoiceCanReceivePayment(Invoice invoice) {

        if (invoice.getStatus() == InvoiceStatus.DRAFT || invoice.getStatus() == InvoiceStatus.CANCELLED || invoice.getStatus() == InvoiceStatus.PAID)

            throw new InvalidResourceStateException("Invoice cannot receive a payment in its current state");
    }

    private void validateAmount(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)

            throw new InvalidRequestException("Payment amount must be greater than zero");
    }

    private void validateNotOverBalance(BigDecimal amount, BigDecimal balance) {
        if (balance == null){
            throw new InvalidResourceStateException("Invoice balance has not been calculated");
        }

        if (amount.compareTo(balance) > 0) {
            throw new BusinessRuleException("Payment amount cannot exceed the outstanding invoice balance");
        }
    }

    private void updateStatus(Invoice invoice) {
        if (invoice.getAmountPaid().compareTo(BigDecimal.ZERO) == 0){
            invoice.setStatus(InvoiceStatus.SENT);
        }
        else if (invoice.getBalanceDue().compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        }
        else invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        invoiceRepository.save(invoice);
    }

    private void validateId(Long id, String resource) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException(resource + " id must be greater than zero");
        }
    }
}
