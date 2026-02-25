package com.bank.corebackend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bank.corebackend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}