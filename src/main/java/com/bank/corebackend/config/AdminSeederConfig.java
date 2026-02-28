package com.bank.corebackend.config;

import com.bank.corebackend.model.User;
import com.bank.corebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminSeederConfig {

    @Bean
    public CommandLineRunner seedAdminUser(
            UserRepository userRepository,
            @Value("${app.admin.enabled:true}") boolean adminEnabled,
            @Value("${app.admin.username:admin}") String adminUsername,
            @Value("${app.admin.password:admin123}") String adminPassword
    ) {
        return args -> {
            if (!adminEnabled || userRepository.findByUsername(adminUsername).isPresent()) {
                return;
            }

            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPassword(adminPassword);
            admin.setRole("ADMIN");
            userRepository.save(admin);
        };
    }
}
