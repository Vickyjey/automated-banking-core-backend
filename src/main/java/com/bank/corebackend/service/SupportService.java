package com.bank.corebackend.service;

import com.bank.corebackend.dto.SupportConversationSummary;
import com.bank.corebackend.dto.SupportMessageRequest;
import com.bank.corebackend.model.SupportMessage;
import com.bank.corebackend.repository.SupportMessageRepository;
import com.bank.corebackend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SupportService {

    private final SupportMessageRepository supportMessageRepository;
    private final UserRepository userRepository;

    public SupportService(SupportMessageRepository supportMessageRepository,
                          UserRepository userRepository) {
        this.supportMessageRepository = supportMessageRepository;
        this.userRepository = userRepository;
    }

    public boolean isAdmin(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public SupportMessage sendMessage(Authentication authentication, SupportMessageRequest request) {
        String message = request.getMessage() == null ? "" : request.getMessage().trim();
        if (message.isEmpty()) {
            throw new RuntimeException("Message cannot be empty");
        }

        boolean admin = isAdmin(authentication);
        String role = admin ? "ADMIN" : "USER";
        String username = admin ? request.getUsername() : authentication.getName();

        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }

        username = username.trim();

        if (admin && userRepository.findByUsername(username).isEmpty()) {
            throw new RuntimeException("Target user not found");
        }

        SupportMessage supportMessage =
                new SupportMessage(username, role, message, admin);

        return supportMessageRepository.save(supportMessage);
    }

    public List<SupportMessage> getMessagesForCurrentUser(Authentication authentication) {
        return supportMessageRepository.findByUsernameOrderBySentAtAsc(authentication.getName());
    }

    public List<SupportMessage> getMessagesForUser(String username) {
        return supportMessageRepository.findByUsernameOrderBySentAtAsc(username);
    }

    public List<SupportConversationSummary> getConversationSummaries() {
        List<SupportMessage> allMessages = supportMessageRepository.findAllByOrderBySentAtDesc();
        Map<String, SupportConversationSummary> summaries = new LinkedHashMap<>();
        Map<String, Long> unreadByUser = new LinkedHashMap<>();

        for (SupportMessage message : allMessages) {
            if ("USER".equals(message.getSenderRole()) && !message.isReadByAdmin()) {
                unreadByUser.put(
                        message.getUsername(),
                        unreadByUser.getOrDefault(message.getUsername(), 0L) + 1
                );
            }

            if (!summaries.containsKey(message.getUsername())) {
                summaries.put(
                        message.getUsername(),
                        new SupportConversationSummary(
                                message.getUsername(),
                                message.getMessage(),
                                message.getSentAt(),
                                0
                        )
                );
            }
        }

        List<SupportConversationSummary> result = new ArrayList<>();
        for (SupportConversationSummary summary : summaries.values()) {
            long unread = unreadByUser.getOrDefault(summary.getUsername(), 0L);
            result.add(
                    new SupportConversationSummary(
                            summary.getUsername(),
                            summary.getLatestMessage(),
                            summary.getLatestTime(),
                            unread
                    )
            );
        }

        return result;
    }

    @Transactional
    public int markConversationRead(String username) {
        List<SupportMessage> unreadMessages =
                supportMessageRepository.findByUsernameAndSenderRoleAndReadByAdminFalse(
                        username, "USER");

        if (unreadMessages.isEmpty()) {
            return 0;
        }

        for (SupportMessage message : unreadMessages) {
            message.setReadByAdmin(true);
        }

        supportMessageRepository.saveAll(unreadMessages);
        return unreadMessages.size();
    }
}
