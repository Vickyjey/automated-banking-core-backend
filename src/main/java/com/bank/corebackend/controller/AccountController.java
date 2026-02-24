package com.bank.corebackend.controller;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.bank.corebackend.service.AccountService;
import com.bank.corebackend.model.Account;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public Account create(@RequestBody Account account) {
        return accountService.createAccount(account);
    }

    @GetMapping("/{accountNumber}")
    public Account get(@PathVariable String accountNumber) {
        return accountService.getAccount(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
}