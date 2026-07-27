package com.ezra_anotida.invoice_maker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "currencies")
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    @Size(max = 10)
    @Column(nullable = false, unique = true)
    private String code;

    @NotBlank
    @Size(max = 10)
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Size(max = 10)
    @Column(nullable = false)
    private String symbol;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal exchangeRateToBase;

    @Column(nullable = false)
    private Boolean active = true;

    private Boolean defaultCurrency;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Currency(){

    }

    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if(active = null){
            active = true;
        }
    }
    @PreUpdate
    public void preUpdate(){
        updatedAt = LocalDateTime.now();
    }

    //Getters
    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getExchangeRateToBase() {
        return exchangeRateToBase;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Boolean getDefaultCurrency() {
        return defaultCurrency;
    }


    //Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setExchangeRateToBase(BigDecimal exchangeRateToBase) {
        this.exchangeRateToBase = exchangeRateToBase;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDefaultCurrency(Boolean defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
}
