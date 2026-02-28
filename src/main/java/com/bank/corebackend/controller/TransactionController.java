package com.bank.corebackend.controller;

import com.bank.corebackend.model.Transaction;
import com.bank.corebackend.repository.TransactionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Get all transactions (Admin use)
    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // 🔥 Get transactions for specific account (User use)
    @GetMapping("/account/{accountNumber}")
    public List<Transaction> getTransactionsByAccount(
            @PathVariable String accountNumber) {

        List<Transaction> sent =
                transactionRepository.findByFromAccount(accountNumber);

        List<Transaction> received =
                transactionRepository.findByToAccount(accountNumber);

        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.addAll(sent);
        allTransactions.addAll(received);

        return allTransactions;
    }
}