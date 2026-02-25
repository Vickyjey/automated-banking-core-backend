package com.bank.corebackend.controller;

import com.bank.corebackend.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.bank.corebackend.service.AccountService;
import com.bank.corebackend.model.Account;
import com.bank.corebackend.dto.TransferRequest;

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
    public ApiResponse transfer(@Valid @RequestBody TransferRequest request) {

        accountService.transfer(
                request.getFromAccount(),
                request.getToAccount(),
                request.getAmount()
        );

        return new ApiResponse("Transfer Successful", null);
    }
}