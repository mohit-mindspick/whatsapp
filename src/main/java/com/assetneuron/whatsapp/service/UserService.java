package com.assetneuron.whatsapp.service;

import com.assetneuron.whatsapp.config.ReadOnly;
import com.assetneuron.whatsapp.dto.SupervisorDTO;
import com.assetneuron.whatsapp.dto.UserDTO;
import com.assetneuron.whatsapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @ReadOnly
    public UserDTO getUserByPhoneNumber(String phoneNumber) {
        log.info("Getting user by phone number: {}", phoneNumber);
        
        List<UserDTO> results = userRepository.findUserWithTeamByPhoneNumber(phoneNumber);
        
        if (results.isEmpty()) {
            throw new RuntimeException("User not found with phone number: " + phoneNumber);
        }
        
        return results.get(0);
    }

    @ReadOnly
    public SupervisorDTO getSupervisorByPhoneNumber(String phoneNumber) {
        log.info("Getting supervisor details by phone number: {}", phoneNumber);
        
        // TODO: Replace with actual database query logic
        // For now, returning dummy data
        return SupervisorDTO.builder()
                .supervisorId(UUID.randomUUID())
                .name("John Supervisor")
                .contact("+1234567890")
                .build();
    }
}

