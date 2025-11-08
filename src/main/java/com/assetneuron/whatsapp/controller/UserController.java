package com.assetneuron.whatsapp.controller;

import com.assetneuron.whatsapp.common.model.ApiResponse;
import com.assetneuron.whatsapp.dto.SupervisorDTO;
import com.assetneuron.whatsapp.dto.UserDTO;
import com.assetneuron.whatsapp.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "User APIs", description = "APIs for managing WhatsApp users")
public class UserController {

    private final UserService userService;

    @GetMapping("/by-phone")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByPhoneNumber(
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestHeader(value = "X-Tenant-Id", required = true) UUID tenantId,
            Authentication authentication) {

        try {
            UserDTO userDTO = userService.getUserByPhoneNumber(phoneNumber);

            return ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                    .success(true)
                    .data(userDTO)
                    .message("User retrieved successfully")
                    .build());
        } catch (RuntimeException e) {
            log.error("Error retrieving user by phone number: {}", phoneNumber, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<UserDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error retrieving user by phone number: {}", phoneNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<UserDTO>builder()
                            .success(false)
                            .message("Failed to retrieve user: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/supervisor/by-phone")
    public ResponseEntity<ApiResponse<SupervisorDTO>> getSupervisorByPhoneNumber(
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestHeader(value = "X-Tenant-Id", required = true) UUID tenantId,
            Authentication authentication) {

        try {
            SupervisorDTO supervisorDTO = userService.getSupervisorByPhoneNumber(phoneNumber);

            return ResponseEntity.ok(ApiResponse.<SupervisorDTO>builder()
                    .success(true)
                    .data(supervisorDTO)
                    .message("Supervisor details retrieved successfully")
                    .build());
        } catch (RuntimeException e) {
            log.error("Error retrieving supervisor by phone number: {}", phoneNumber, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<SupervisorDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error retrieving supervisor by phone number: {}", phoneNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<SupervisorDTO>builder()
                            .success(false)
                            .message("Failed to retrieve supervisor: " + e.getMessage())
                            .build());
        }
    }
}

