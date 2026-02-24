package com.bank.corebackend.service;

import org.springframework.stereotype.Service;
import java.util.Optional;
import com.bank.corebackend.repository.AccountRepository;
import com.bank.corebackend.model.Account;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }
}