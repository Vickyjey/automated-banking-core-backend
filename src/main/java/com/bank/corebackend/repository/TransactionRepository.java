package com.bank.corebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bank.corebackend.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}