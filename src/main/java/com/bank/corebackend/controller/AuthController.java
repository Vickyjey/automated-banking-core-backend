package com.bank.corebackend.controller;

import com.bank.corebackend.dto.AuthRequest;
import com.bank.corebackend.dto.ForgotPasswordRequest;
import com.bank.corebackend.model.User;
import com.bank.corebackend.repository.UserRepository;
import com.bank.corebackend.service.JwtService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    public AuthController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.setRole("USER");
        userRepository.save(user);
        return "User Registered";
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Password is incorrect"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Password is incorrect");
        }

        return jwtService.generateToken(user);
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String newPassword = request.getNewPassword() == null ? "" : request.getNewPassword().trim();

        if (username.isEmpty() || newPassword.isEmpty()) {
            throw new RuntimeException("Username and new password are required.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found. Please create an account."));

        user.setPassword(newPassword);
        userRepository.save(user);
        return "Password updated successfully. Please login.";
    }
}
