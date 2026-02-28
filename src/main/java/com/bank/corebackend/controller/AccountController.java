package com.bank.corebackend.controller;

import com.bank.corebackend.dto.ApiResponse;
import com.bank.corebackend.dto.DepositRequest;
import com.bank.corebackend.dto.TransferResponse;
import com.bank.corebackend.model.Transaction;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.bank.corebackend.service.AccountService;
import com.bank.corebackend.model.Account;
import com.bank.corebackend.dto.TransferRequest;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    // Constructor Injection (Correct)
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Create Account
    @PostMapping
    public Account create(@RequestBody Account account) {
        return accountService.createAccount(account);
    }

    // Get Account by Account Number
    @GetMapping("/{accountNumber}")
    public Account get(@PathVariable String accountNumber) {
        return accountService.getAccount(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    // Transfer Money using DTO
    @PostMapping("/transfer")
    public TransferResponse transfer(@RequestBody TransferRequest request) {

        Double remainingBalance = accountService.transfer(
                request.getFromAccount(),
                request.getToAccount(),
                request.getAmount()
        );

        return new TransferResponse(
                "Transfer Successful",
                remainingBalance
        );
    }
    @PostMapping("/deposit")
    public String deposit(@RequestBody DepositRequest request) {

        Double updatedBalance = accountService.deposit(
                request.getAccountNumber(),
                request.getAmount()
        );

        return "Deposit Successful. Updated Balance: " + updatedBalance;
    }
}