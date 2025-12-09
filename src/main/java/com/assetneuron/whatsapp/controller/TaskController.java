package com.assetneuron.whatsapp.controller;

import com.assetneuron.whatsapp.common.adaptor.HttpClientResponse;
import com.assetneuron.whatsapp.common.adaptor.RequestTokenUtil;
import com.assetneuron.whatsapp.common.model.ApiResponse;
import com.assetneuron.whatsapp.dto.ResponseCodes;
import com.assetneuron.whatsapp.dto.SaveTaskChecklistItemsRequest;
import com.assetneuron.whatsapp.dto.TaskChecklistResponseDTO;
import com.assetneuron.whatsapp.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final RequestTokenUtil requestTokenUtil;

    @GetMapping("/workorder/{workOrderId}/task/{taskId}/checklist")
    public ResponseEntity<ApiResponse<TaskChecklistResponseDTO>> getTaskChecklistItems(
            @PathVariable UUID workOrderId,
            @PathVariable UUID taskId,
            Authentication authentication) {

        try {
            // Extract tenant ID from JWT token (validates token and tenant ID presence)
            UUID tenantId = requestTokenUtil.getTenantIdFromToken();
            TaskChecklistResponseDTO response = taskService.getTaskChecklistItems(workOrderId, taskId, tenantId);

            return ResponseEntity.ok(ApiResponse.<TaskChecklistResponseDTO>builder()
                    .success(true)
                    .data(response)
                    .message(ResponseCodes.CHECKLIST_ITEMS_RETRIEVAL_SUCCESSFUL.name())
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving checklist items for work order id: {}, task id: {}",
                    workOrderId, taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<TaskChecklistResponseDTO>builder()
                            .success(false)
                            .message(ResponseCodes.ERR_FAILED_TO_RETRIEVE_CHECKLIST_ITEMS.name())
                            .build());
        }
    }

   /* @PostMapping("/save-checklist-item-response")
    public ResponseEntity<ApiResponse<Object>> saveChecklistItemResponse(
            @Valid @RequestBody SaveTaskChecklistItemsRequest request,
            Authentication authentication) {

        try {
            // Extract tenant ID from JWT token (validates token and tenant ID presence)
            UUID tenantId = requestTokenUtil.getTenantIdFromToken();
            HttpClientResponse<Object> response = taskService.saveChecklistItemResponse(request, tenantId);

            // Return the response from work order service
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.<Object>builder()
                                .success(true)
                                .data(response.getBody())
                                .message(ResponseCodes.CHECKLIST_ITEM_RESPONSES_SAVED_SUCCESSFUL.name())
                                .build());
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.<Object>builder()
                                .success(false)
                                .data(response.getBody())
                                .message(ResponseCodes.ERR_FAILED_TO_SAVE_CHECKLIST_ITEM_RESPONSES.name())
                                .build());
            }
        } catch (Exception e) {
            log.error("Error saving checklist item responses for work order id: {}, task id: {}",
                    request.getWorkOrderId(), request.getTaskId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Object>builder()
                            .success(false)
                            .message(ResponseCodes.ERR_FAILED_TO_SAVE_CHECKLIST_ITEM_RESPONSES.name())
                            .build());
        }
    }*/
}

