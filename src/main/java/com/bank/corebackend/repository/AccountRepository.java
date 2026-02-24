package com.bank.corebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.bank.corebackend.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
}