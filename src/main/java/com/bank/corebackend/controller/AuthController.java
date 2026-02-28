package com.bank.corebackend.controller;

import com.bank.corebackend.dto.AuthRequest;
import com.bank.corebackend.service.JwtService;
import org.springframework.web.bind.annotation.*;
import com.bank.corebackend.model.User;
import com.bank.corebackend.repository.UserRepository;
import com.bank.corebackend.security.JwtUtil;
import java.util.Optional;

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
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtService.generateToken(user);   // 🔥 pass full user
    }
}