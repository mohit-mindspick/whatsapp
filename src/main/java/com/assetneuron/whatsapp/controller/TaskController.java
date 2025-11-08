package com.assetneuron.whatsapp.controller;

import com.assetneuron.whatsapp.common.model.ApiResponse;
import com.assetneuron.whatsapp.dto.TaskChecklistResponseDTO;
import com.assetneuron.whatsapp.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Task APIs", description = "APIs for managing work order tasks")
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/workorder/{workOrderId}/task/{taskId}/checklist")
    public ResponseEntity<ApiResponse<TaskChecklistResponseDTO>> getTaskChecklistItems(
            @PathVariable UUID workOrderId,
            @PathVariable UUID taskId,
            @RequestHeader(value = "X-Tenant-Id", required = true) UUID tenantId,
            Authentication authentication) {

        try {
            TaskChecklistResponseDTO response = taskService.getTaskChecklistItems(workOrderId, taskId, tenantId);

            return ResponseEntity.ok(ApiResponse.<TaskChecklistResponseDTO>builder()
                    .success(true)
                    .data(response)
                    .message("Checklist items retrieved successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving checklist items for work order id: {}, task id: {}",
                    workOrderId, taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<TaskChecklistResponseDTO>builder()
                            .success(false)
                            .message("Failed to retrieve checklist items: " + e.getMessage())
                            .build());
        }
    }
}

