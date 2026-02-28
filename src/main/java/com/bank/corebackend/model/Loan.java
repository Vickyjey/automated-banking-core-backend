package com.bank.corebackend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;
    private String loanType;
    private Double loanAmount;
    private String status; // PENDING, APPROVED
    private String requestedBy;
    private LocalDateTime requestedAt;

    public Loan() {}

    public Loan(String accountNumber, String loanType, Double loanAmount, String status, String requestedBy) {
        this.accountNumber = accountNumber;
        this.loanType = loanType;
        this.loanAmount = loanAmount;
        this.status = status;
        this.requestedBy = requestedBy;
        this.requestedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public String getAccountNumber() { return accountNumber; }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getLoanAmount() { return loanAmount; }

    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
}
