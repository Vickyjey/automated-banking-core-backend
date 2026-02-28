package com.bank.corebackend.service;

import org.springframework.stereotype.Service;
import java.util.Optional;
import com.bank.corebackend.repository.AccountRepository;
import com.bank.corebackend.model.Account;
import org.springframework.transaction.annotation.Transactional;
import com.bank.corebackend.model.Transaction;
import com.bank.corebackend.repository.TransactionRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    @Transactional
    public Double transfer(String fromAcc, String toAcc, Double amount) {

        Account sender = accountRepository.findByAccountNumber(fromAcc)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Account receiver = accountRepository.findByAccountNumber(toAcc)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (sender.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        accountRepository.save(sender);
        accountRepository.save(receiver);

        Transaction txn = new Transaction(fromAcc, toAcc, amount);
        transactionRepository.save(txn);

        return sender.getBalance(); // return updated balance
    }
    @Transactional
    public Double deposit(String accountNumber, Double amount) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance() + amount);

        accountRepository.save(account);

        Transaction txn = new Transaction(
                "SELF",
                accountNumber,
                amount
        );

        transactionRepository.save(txn);

        return account.getBalance();
    }
    public Optional<Account> getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }
}