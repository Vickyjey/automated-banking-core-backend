package com.bank.corebackend.repository;

import com.bank.corebackend.model.SupportMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportMessageRepository extends JpaRepository<SupportMessage, Long> {
    List<SupportMessage> findByUsernameOrderBySentAtAsc(String username);
    List<SupportMessage> findByUsernameAndSenderRoleAndReadByAdminFalse(String username, String senderRole);
    List<SupportMessage> findAllByOrderBySentAtDesc();
}
