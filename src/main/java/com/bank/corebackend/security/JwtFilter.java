package com.bank.corebackend.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.bank.corebackend.repository.UserRepository;
import com.bank.corebackend.model.User;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public JwtFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);
            String username = JwtUtil.extractUsername(token);

            Optional<User> userOpt = userRepository.findByUsername(username);

            if (userOpt.isPresent()) {

                User user = userOpt.get();

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singletonList(
                                        new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}