package com.assetneuron.whatsapp.common.service;

import com.assetneuron.whatsapp.common.model.UserSession;
import com.assetneuron.whatsapp.common.repository.UserSessionRepository;
import com.assetneuron.whatsapp.config.ReadOnly;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserSessionService {

    private final UserSessionRepository userSessionRepository;

    @ReadOnly
    public UserSession getUserSessionBySessionId(UUID sessionId) {
        return userSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> {
                    log.warn("Session not found for sessionId: {}", sessionId);
                    return new RuntimeException("ERR_SESSION_NOT_FOUND");
                });
    }
}

