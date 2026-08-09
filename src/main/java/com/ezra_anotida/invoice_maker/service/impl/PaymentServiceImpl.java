package com.ezra_anotida.invoice_maker.service.impl;

import com.ezra_anotida.invoice_maker.enums.InvoiceStatus;
import com.ezra_anotida.invoice_maker.exception.BusinessRuleException;
import com.ezra_anotida.invoice_maker.exception.InvalidRequestException;
import com.ezra_anotida.invoice_maker.exception.InvalidResourceStateException;
import com.ezra_anotida.invoice_maker.exception.ResourceNotFoundException;
import com.ezra_anotida.invoice_maker.dto.payment.CreatePaymentRequest;
import com.ezra_anotida.invoice_maker.dto.payment.PaymentResponse;
import com.ezra_anotida.invoice_maker.dto.payment.UpdatePaymentRequest;
import com.ezra_anotida.invoice_maker.entity.Invoice;
import com.ezra_anotida.invoice_maker.entity.Payment;
import com.ezra_anotida.invoice_maker.mapper.PaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ezra_anotida.invoice_maker.repository.InvoiceRepository;
import com.ezra_anotida.invoice_maker.repository.PaymentRepository;
import com.ezra_anotida.invoice_maker.service.PaymentService;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceCalculationService invoiceCalculationService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentMapper paymentMapper, InvoiceRepository invoiceRepository, InvoiceCalculationService invoiceCalculationService) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.invoiceRepository = invoiceRepository;
        this.invoiceCalculationService = invoiceCalculationService;
    }

    @Override
    public PaymentResponse recordPayment(Long invoiceId, CreatePaymentRequest request) {

        Invoice invoice = findInvoiceById(invoiceId);

        validateInvoiceCanReceivePayment(invoice);

        validatePaymentAmount(request.amount());

        validatePaymentDoesNotExceedBalance(request.amount(), invoice.getBalanceDue());

        Payment payment = paymentMapper.toEntity(request);

        payment.setInvoice(invoice);

        Payment savedPayment = paymentRepository.save(payment);

        paymentRepository.flush();

        recalculateInvoicePayments(invoice);

        updateInvoicePaymentStatus(invoice);

        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long paymentId) {

        Payment payment = findPaymentById(paymentId);

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {

        List<Payment> payments = paymentRepository.findAll();

        return paymentMapper.toResponseList(payments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByInvoice(Long invoiceId) {

        findInvoiceById(invoiceId);

        List<Payment> payments = paymentRepository.findByInvoiceId(invoiceId);

        return paymentMapper.toResponseList(payments);
    }

    @Override
    public PaymentResponse updatePayment(Long paymentId, UpdatePaymentRequest request) {

        Payment existingPayment  = findPaymentById(paymentId);

        if(request.amount() != null){
            validatePaymentAmount(request.amount());
        }

        Invoice invoice = existingPayment.getInvoice();

        paymentMapper.updateEntityFromRequest(request,existingPayment);

        Payment updatedPayment = paymentRepository.save(existingPayment);

        paymentRepository.flush();

        recalculateInvoicePayments(invoice);

        updateInvoicePaymentStatus(invoice);

        return paymentMapper.toResponse(updatedPayment);
    }

    @Override
    public void deletePayment(Long paymentId) {

        Payment payment = findPaymentById(paymentId);

        Invoice invoice = payment.getInvoice();

        paymentRepository.delete(payment);

        paymentRepository.flush();

        recalculateInvoicePayments(invoice);

    }

    //Helper Methods
    private Payment findPaymentById(Long paymentId) {

        if(paymentId == null){
            throw new InvalidRequestException("Payment ID cannot be null");
        }

        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
    }

    private Invoice findInvoiceById(Long invoiceId) {
        if (invoiceId == null){
            throw new InvalidRequestException("Invoice ID cannot be null");
        }

        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
    }

    private void validatePaymentAmount(BigDecimal amount) {
        if(amount == null){
            throw new InvalidRequestException("Payment amount is required");
        }

        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidRequestException("Payment amount must be greater than zero");
        }
    }

    private void recalculateInvoicePayments(Invoice invoice) {
        BigDecimal totalPaid = paymentRepository
                .findByInvoiceId(invoice.getId())
                .stream()
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoice.setAmountPaid(totalPaid);

        invoiceCalculationService.recalculateInvoiceTotals(invoice);

        invoiceRepository.save(invoice);

    }

    private void validateInvoiceCanReceivePayment(Invoice invoice){

        if(invoice.getStatus() ==  InvoiceStatus.DRAFT){
            throw new InvalidResourceStateException("A draft invoice cannot receive payments");
        }

        if(invoice.getStatus() == InvoiceStatus.CANCELLED){
            throw new InvalidResourceStateException("A cancelled invoice cannot receive payments");
        }

        if(invoice.getStatus() == InvoiceStatus.PAID){
            throw new InvalidResourceStateException("A paid invoice cannot receive another payment");
        }
    }

    private void validatePaymentDoesNotExceedBalance(BigDecimal paymentAmount, BigDecimal balanceDue){

        if(balanceDue == null){
            throw new InvalidResourceStateException("Invalid balance has not been calculated");
        }

        if(paymentAmount.compareTo(balanceDue) > 0){
            throw new BusinessRuleException("Payment amount cannot exceed" + "the outstanding invoice balance");
        }
    }

    private void updateInvoicePaymentStatus(Invoice invoice){

        if(invoice.getAmountPaid().compareTo(BigDecimal.ZERO) == 0){
            invoice.setStatus(InvoiceStatus.SENT);
        }else if (invoice.getBalanceDue().compareTo(BigDecimal.ZERO) == 0){
            invoice.setStatus(InvoiceStatus.PAID);
        }else{
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        invoiceRepository.save(invoice);
    }
}
