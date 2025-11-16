package com.assetneuron.whatsapp.controller;

import com.assetneuron.whatsapp.common.model.ApiResponse;
import com.assetneuron.whatsapp.dto.LogHoursRequest;
import com.assetneuron.whatsapp.dto.MyWorkDTO;
import com.assetneuron.whatsapp.dto.ViewMyWorkRequest;
import com.assetneuron.whatsapp.dto.ViewWorkOrderDetailRequest;
import com.assetneuron.whatsapp.dto.WorkItemDetailDTO;
import com.assetneuron.whatsapp.dto.WorkItemTasksResponseDTO;
import com.assetneuron.whatsapp.service.WorkItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workorder")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "WorkOrder APIs", description = "APIs for managing work orders")
public class WorkOrderController {

    private final WorkItemService workItemService;

    @GetMapping("/viewmywork")
    @PreAuthorize("@authConfig.isSecurityEnabled() ? hasAuthority('WHATSAPP_WORKORDER_READ') : true")
    public ResponseEntity<ApiResponse<List<MyWorkDTO>>> viewMyWork(
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestHeader(value = "X-Tenant-Id", required = true) UUID tenantId,
            Authentication authentication) {

        try {
            ViewMyWorkRequest request = ViewMyWorkRequest.builder()
                    .phoneNumber(phoneNumber)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();

            List<MyWorkDTO> myWorkList = workItemService.viewMyWork(request);

            return ResponseEntity.ok(ApiResponse.<List<MyWorkDTO>>builder()
                    .success(true)
                    .data(myWorkList)
                    .message("Work items retrieved successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving work items for phone number: {}", phoneNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<MyWorkDTO>>builder()
                            .success(false)
                            .message("Failed to retrieve work items: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/detail")
    @PreAuthorize("@authConfig.isSecurityEnabled() ? hasAuthority('WHATSAPP_WORKORDER_READ') : true")
    public ResponseEntity<ApiResponse<WorkItemDetailDTO>> getWorkOrderDetail(
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("workItemId") UUID workItemId,
            @RequestParam("itemType") String itemType,
            @RequestHeader(value = "X-Tenant-Id", required = true) UUID tenantId,
            Authentication authentication) {

        try {
            ViewWorkOrderDetailRequest request = ViewWorkOrderDetailRequest.builder()
                    .phoneNumber(phoneNumber)
                    .workItemId(workItemId)
                    .itemType(com.assetneuron.whatsapp.enums.WorkItemType.fromCode(itemType))
                    .build();

            WorkItemDetailDTO workOrderDetail = workItemService.getWorkItemDetail(request);

            return ResponseEntity.ok(ApiResponse.<WorkItemDetailDTO>builder()
                    .success(true)
                    .data(workOrderDetail)
                    .message("Work order detail retrieved successfully")
                    .build());
        } catch (IllegalArgumentException e) {
            log.error("Invalid item type: {}", itemType, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<WorkItemDetailDTO>builder()
                            .success(false)
                            .message("Invalid item type: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error retrieving work order detail for phone number: {}, work item id: {}",
                    phoneNumber, workItemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<WorkItemDetailDTO>builder()
                            .success(false)
                            .message("Failed to retrieve work order detail: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{workItemId}/tasks")
    @PreAuthorize("@authConfig.isSecurityEnabled() ? hasAuthority('WHATSAPP_WORKORDER_READ') : true")
    public ResponseEntity<ApiResponse<WorkItemTasksResponseDTO>> getWorkItemTasks(
            @PathVariable UUID workItemId,
            @RequestHeader(value = "X-Tenant-Id", required = true) UUID tenantId,
            Authentication authentication) {

        try {
            WorkItemTasksResponseDTO tasksResponse = workItemService.getWorkItemTasks(workItemId, tenantId);

            return ResponseEntity.ok(ApiResponse.<WorkItemTasksResponseDTO>builder()
                    .success(true)
                    .data(tasksResponse)
                    .message("Tasks retrieved successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving tasks for work item id: {}", workItemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<WorkItemTasksResponseDTO>builder()
                            .success(false)
                            .message("Failed to retrieve tasks: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/log-hours")
    @PreAuthorize("@authConfig.isSecurityEnabled() ? hasAuthority('WHATSAPP_WORKORDER_UPDATE') : true")
    public ResponseEntity<ApiResponse<Void>> logHours(
            @Valid @RequestBody LogHoursRequest request,
            @RequestHeader(value = "X-Tenant-Id", required = true) UUID tenantId,
            Authentication authentication) {

        try {
            workItemService.logHours(request, tenantId);

            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Hours logged successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error logging hours for work item id: {}, user id: {}",
                    request.getWorkItemId(), request.getUserId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to log hours: " + e.getMessage())
                            .build());
        }
    }
}

