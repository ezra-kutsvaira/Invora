package com.ezra_anotida.invoice_maker.entity;

import com.ezra_anotida.invoice_maker.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDate paymentDate;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Size(max = 100)
    private String referenceNumber;

    @Column(length = 1000)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL)
    private Receipt receipt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Payment() {
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();

        if (paymentDate == null) {
            paymentDate = LocalDate.now();
        }
    }

    public Long getId() {
        return id;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public String getNotes() {
        return notes;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }
}
