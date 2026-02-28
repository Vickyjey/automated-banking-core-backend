package com.bank.corebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bank.corebackend.model.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromAccount(String fromAccount);
    List<Transaction> findByToAccount(String toAccount);
}