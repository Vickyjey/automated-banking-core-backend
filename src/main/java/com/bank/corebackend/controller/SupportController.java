package com.bank.corebackend.controller;

import com.bank.corebackend.dto.SupportConversationSummary;
import com.bank.corebackend.dto.SupportMessageRequest;
import com.bank.corebackend.model.SupportMessage;
import com.bank.corebackend.service.SupportService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    private final SupportService supportService;

    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    @PostMapping("/messages")
    public SupportMessage sendMessage(@RequestBody SupportMessageRequest request,
                                      Authentication authentication) {
        return supportService.sendMessage(authentication, request);
    }

    @GetMapping("/messages/me")
    public List<SupportMessage> myMessages(Authentication authentication) {
        return supportService.getMessagesForCurrentUser(authentication);
    }

    @GetMapping("/admin/conversations")
    public List<SupportConversationSummary> adminConversations() {
        return supportService.getConversationSummaries();
    }

    @GetMapping("/admin/messages/{username}")
    public List<SupportMessage> adminMessages(@PathVariable String username) {
        return supportService.getMessagesForUser(username);
    }

    @PutMapping("/admin/messages/{username}/read")
    public Map<String, Object> markRead(@PathVariable String username) {
        int updated = supportService.markConversationRead(username);
        return Map.of("updated", updated);
    }
}
