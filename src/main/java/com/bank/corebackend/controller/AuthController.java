package com.bank.corebackend.controller;

import org.springframework.web.bind.annotation.*;
import com.bank.corebackend.model.User;
import com.bank.corebackend.repository.UserRepository;
import com.bank.corebackend.security.JwtUtil;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        userRepository.save(user);
        return "User Registered";
    }

    @PostMapping("/login")
    public String login(@RequestBody User loginUser) {

        Optional<User> user = userRepository.findByUsername(loginUser.getUsername());

        if (user.isPresent() &&
                user.get().getPassword().equals(loginUser.getPassword())) {

            return JwtUtil.generateToken(loginUser.getUsername());
        }

        throw new RuntimeException("Invalid credentials");
    }
}