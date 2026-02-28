package com.bank.corebackend.dto;

import java.time.LocalDateTime;

public class SupportConversationSummary {

    private String username;
    private String latestMessage;
    private LocalDateTime latestTime;
    private long unreadCount;

    public SupportConversationSummary(String username, String latestMessage, LocalDateTime latestTime, long unreadCount) {
        this.username = username;
        this.latestMessage = latestMessage;
        this.latestTime = latestTime;
        this.unreadCount = unreadCount;
    }

    public String getUsername() {
        return username;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public LocalDateTime getLatestTime() {
        return latestTime;
    }

    public long getUnreadCount() {
        return unreadCount;
    }
}
