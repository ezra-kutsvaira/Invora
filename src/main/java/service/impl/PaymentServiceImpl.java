package service.impl;

import dto.payment.CreatePaymentRequest;
import dto.payment.PaymentResponse;
import dto.payment.UpdatePaymentRequest;
import entity.Invoice;
import entity.Payment;
import jakarta.persistence.EntityNotFoundException;
import mapper.PaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.InvoiceRepository;
import repository.PaymentRepository;
import service.PaymentService;

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

        validatePaymentAmount(request.amount());

        Payment payment = paymentMapper.toEntity(request);
        payment.setInvoice(invoice);

        Payment savedPayment = paymentRepository.save(payment);

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
            throw new IllegalArgumentException("Payment id cannot be null");
        }

        return paymentRepository.findById(paymentId)
                .orElseThrow(()-> new EntityNotFoundException("Payment with id" + paymentId + "was not found"));
    }

    private Invoice findInvoiceById(Long invoiceId) {
        if (invoiceId == null){
            throw new IllegalArgumentException("Invoice id cannot be null");
        }

        return invoiceRepository.findById(invoiceId)
                .orElseThrow(()-> new EntityNotFoundException("Invoice ID with" + invoiceId + "not found"));
    }

    private void validatePaymentAmount(BigDecimal amount) {
        if(amount == null){
            throw new IllegalArgumentException("Payment amount is required");
        }

        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw  new IllegalArgumentException("Payment amount cannot be negative");
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

}
