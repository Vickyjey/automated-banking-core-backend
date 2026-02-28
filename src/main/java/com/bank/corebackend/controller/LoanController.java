package com.bank.corebackend.controller;

import com.bank.corebackend.model.Loan;
import com.bank.corebackend.repository.LoanRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanRepository loanRepository;

    public LoanController(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @PostMapping
    public Loan applyLoan(@RequestBody Loan loan, Authentication authentication) {
        String accountNumber = loan.getAccountNumber() == null ? "" : loan.getAccountNumber().trim();
        String loanType = loan.getLoanType() == null ? "" : loan.getLoanType().trim();
        Double loanAmount = loan.getLoanAmount();

        if (accountNumber.isEmpty()) {
            throw new RuntimeException("Account number is required");
        }

        if (loanType.isEmpty()) {
            throw new RuntimeException("Loan type is required");
        }

        if (loanAmount == null || loanAmount <= 0) {
            throw new RuntimeException("Loan amount must be greater than zero");
        }

        loan.setAccountNumber(accountNumber);
        loan.setLoanType(loanType);
        loan.setStatus("PENDING");
        loan.setRequestedBy(authentication.getName());
        if (loan.getRequestedAt() == null) {
            loan.setRequestedAt(java.time.LocalDateTime.now());
        }
        return loanRepository.save(loan);
    }

    @GetMapping
    public List<Loan> getAllLoans(Authentication authentication) {
        if (isAdmin(authentication)) {
            return loanRepository.findAllByOrderByIdDesc();
        }

        return loanRepository.findByRequestedByOrderByIdDesc(authentication.getName());
    }

    @GetMapping("/account/{accountNumber}")
    public List<Loan> getLoansByAccount(@PathVariable String accountNumber, Authentication authentication) {
        String normalizedAccount = accountNumber == null ? "" : accountNumber.trim();
        if (normalizedAccount.isEmpty()) {
            throw new RuntimeException("Account number is required");
        }

        if (isAdmin(authentication)) {
            return loanRepository.findByAccountNumberOrderByIdDesc(normalizedAccount);
        }

        return loanRepository.findByAccountNumberAndRequestedByOrderByIdDesc(
                normalizedAccount,
                authentication.getName()
        );
    }

    @PutMapping("/{loanId}/approve")
    public Loan approveLoan(@PathVariable Long loanId, Authentication authentication) {
        if (!isAdmin(authentication)) {
            throw new RuntimeException("Admin access required");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        loan.setStatus("APPROVED");
        return loanRepository.save(loan);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .anyMatch("ROLE_ADMIN"::equals);
    }
}
