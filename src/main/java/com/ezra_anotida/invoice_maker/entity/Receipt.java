package com.ezra_anotida.invoice_maker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "receipts")
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String receiptNumber;

    @NotNull
    @Column(nullable = false)
    private LocalDate receiptDate;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Receipt() {
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();

        if (receiptDate == null) {
            receiptDate = LocalDate.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public LocalDate getReceiptDate() {
        return receiptDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Payment getPayment() {
        return payment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public void setReceiptDate(LocalDate receiptDate) {
        this.receiptDate = receiptDate;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
