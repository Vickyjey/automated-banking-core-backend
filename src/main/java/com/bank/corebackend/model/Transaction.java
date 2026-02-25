package com.bank.corebackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromAccount;
    private String toAccount;
    private Double amount;
    private LocalDateTime transactionTime;

    public Transaction() {}

    public Transaction(String fromAccount, String toAccount, Double amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.transactionTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccount; }
    public Double getAmount() { return amount; }
    public LocalDateTime getTransactionTime() { return transactionTime; }
}