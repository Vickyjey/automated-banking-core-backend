package com.bank.corebackend.repository;

import com.bank.corebackend.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findAllByOrderByIdDesc();
    List<Loan> findByRequestedByOrderByIdDesc(String requestedBy);
    List<Loan> findByAccountNumberAndRequestedByOrderByIdDesc(String accountNumber, String requestedBy);
    List<Loan> findByAccountNumberOrderByIdDesc(String accountNumber);
}
