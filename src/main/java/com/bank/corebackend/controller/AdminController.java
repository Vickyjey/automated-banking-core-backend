package com.bank.corebackend.controller;

import com.bank.corebackend.model.Account;
import com.bank.corebackend.model.Transaction;
import com.bank.corebackend.model.User;
import com.bank.corebackend.repository.AccountRepository;
import com.bank.corebackend.repository.TransactionRepository;
import com.bank.corebackend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Comparator;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AdminController(UserRepository userRepository,
                           AccountRepository accountRepository,
                           TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        users.sort(Comparator.comparing(User::getId));
        return users;
    }

    @GetMapping("/accounts")
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @DeleteMapping("/account/{accountNumber}")
    public String deleteAccount(@PathVariable String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        accountRepository.delete(account);

        return "Account Deleted Successfully";
    }

    @DeleteMapping("/user/{userId}")
    public String deleteUser(@PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);

        return "User Deleted Successfully";
    }
}
