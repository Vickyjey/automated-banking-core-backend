package com.bank.corebackend.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TransferRequest {

    @NotBlank(message = "From account is required")
    private String fromAccount;

    @NotBlank(message = "To account is required")
    private String toAccount;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    public TransferRequest() {}

    // Optional constructor
    public TransferRequest(String fromAccount, String toAccount, Double amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
    }

    // Getter for fromAccount
    public String getFromAccount() {
        return fromAccount;
    }

    // Setter for fromAccount
    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    // Getter for toAccount
    public String getToAccount() {
        return toAccount;
    }

    // Setter for toAccount
    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    // Getter for amount
    public Double getAmount() {
        return amount;
    }

    // Setter for amount
    public void setAmount(Double amount) {
        this.amount = amount;
    }
}