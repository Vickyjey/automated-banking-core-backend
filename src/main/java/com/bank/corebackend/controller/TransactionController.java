package com.bank.corebackend.controller;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;
import com.bank.corebackend.model.Transaction;
import com.bank.corebackend.repository.TransactionRepository;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}