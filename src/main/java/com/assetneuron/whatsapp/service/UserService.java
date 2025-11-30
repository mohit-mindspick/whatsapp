package com.assetneuron.whatsapp.service;

import com.assetneuron.whatsapp.config.ReadOnly;
import com.assetneuron.whatsapp.dto.SupervisorDTO;
import com.assetneuron.whatsapp.dto.UserDTO;
import com.assetneuron.whatsapp.model.User;
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
    public UserDTO getUserByPhoneNumber(String phoneNumber, UUID tenantId) {
        log.info("Getting user by phone number: {} for tenant: {}", phoneNumber, tenantId);

        List<UserDTO> results = userRepository.findUserWithTeamByPhoneNumber(phoneNumber, tenantId);

        if (results.isEmpty()) {
            throw new RuntimeException("User not found with phone number: " + phoneNumber);
        }

        return results.get(0);
    }

    @ReadOnly
    public SupervisorDTO getSupervisorByPhoneNumber(String phoneNumber, UUID tenantId) {
        log.info("Getting supervisor details by phone number: {} for tenant: {}", phoneNumber, tenantId);

        // Find user by phone number with tenant ID filtering
        User user = userRepository.findByPhoneNumberAndActive(phoneNumber, tenantId)
                .orElseThrow(() -> new RuntimeException("User not found with phone number: " + phoneNumber));

        // Check if user has a shift
        if (user.getShift() == null) {
            throw new RuntimeException("User does not have an assigned shift");
        }

        // Verify shift belongs to the same tenant
        if (!user.getShift().getTenantId().equals(tenantId)) {
            throw new RuntimeException("User shift does not belong to the same tenant");
        }

        // Find the first supervisor in the user's shift
        List<User> supervisors = userRepository.findSupervisorsByShiftId(user.getShift().getId(), tenantId);

        if (supervisors.isEmpty()) {
            throw new RuntimeException("No supervisor found for shift: " + user.getShift().getId());
        }

        // Get the first supervisor (ordered by createdAt ASC)
        User supervisor = supervisors.get(0);

        // Build and return SupervisorDTO
        return SupervisorDTO.builder()
                .supervisorId(supervisor.getId())
                .name(supervisor.getFirstName() + " " + supervisor.getLastName())
                .contact(supervisor.getPhoneNumber())
                .build();
    }
}

