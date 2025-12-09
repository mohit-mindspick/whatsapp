package com.assetneuron.whatsapp.controller;

import com.assetneuron.whatsapp.common.adaptor.HttpClientResponse;
import com.assetneuron.whatsapp.common.adaptor.RequestTokenUtil;
import com.assetneuron.whatsapp.common.model.ApiResponse;
import com.assetneuron.whatsapp.dto.AddCommentRequest;
import com.assetneuron.whatsapp.dto.LogHoursRequest;
import com.assetneuron.whatsapp.dto.MyWorkDTO;
import com.assetneuron.whatsapp.dto.ResponseCodes;
import com.assetneuron.whatsapp.dto.SaveAssetRatingRequest;
import com.assetneuron.whatsapp.dto.ViewMyWorkRequest;
import com.assetneuron.whatsapp.dto.ViewWorkOrderDetailRequest;
import com.assetneuron.whatsapp.dto.WorkItemDetailDTO;
import com.assetneuron.whatsapp.dto.WorkItemTasksResponseDTO;
import com.assetneuron.whatsapp.enums.DateFilterType;
import com.assetneuron.whatsapp.enums.WorkItemType;
import com.assetneuron.whatsapp.service.WorkOrderService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workorder")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "WorkOrder APIs", description = "APIs for managing work orders")
public class WorkOrderController {

    private final WorkOrderService workItemService;
    private final RequestTokenUtil requestTokenUtil;

    @GetMapping("/viewmywork")
    public ResponseEntity<ApiResponse<List<MyWorkDTO>>> viewMyWork(
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("dateFilter") DateFilterType dateFilter,
            Authentication authentication) {

        try {
            // Extract tenant ID from JWT token (validates token and tenant ID presence)
            UUID tenantId = requestTokenUtil.getTenantIdFromToken();
            ViewMyWorkRequest request = ViewMyWorkRequest.builder()
                    .phoneNumber(phoneNumber)
                    .dateFilter(dateFilter)
                    .build();

            List<MyWorkDTO> myWorkList = workItemService.viewMyWork(request, tenantId);

            return ResponseEntity.ok(ApiResponse.<List<MyWorkDTO>>builder()
                    .success(true)
                    .data(myWorkList)
                    .message(ResponseCodes.WORK_ITEMS_RETRIEVAL_SUCCESSFUL.name())
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving work items for phone number: {}", phoneNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<MyWorkDTO>>builder()
                            .success(false)
                            .message(ResponseCodes.ERR_FAILED_TO_RETRIEVE_WORK_ITEMS.name())
                            .build());
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<WorkItemDetailDTO>> getWorkOrderDetail(
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("workItemId") UUID workItemId,
            @RequestParam("type") WorkItemType type,
            Authentication authentication) {

        try {
            // Extract tenant ID from JWT token (validates token and tenant ID presence)
            UUID tenantId = requestTokenUtil.getTenantIdFromToken();
            ViewWorkOrderDetailRequest request = ViewWorkOrderDetailRequest.builder()
                    .phoneNumber(phoneNumber)
                    .workItemId(workItemId)
                    .itemType(type)
                    .build();

            WorkItemDetailDTO workOrderDetail = workItemService.getWorkItemDetail(request, tenantId);

            return ResponseEntity.ok(ApiResponse.<WorkItemDetailDTO>builder()
                    .success(true)
                    .data(workOrderDetail)
                    .message(ResponseCodes.WORK_ORDER_DETAIL_RETRIEVAL_SUCCESSFUL.name())
                    .build());
        } catch (IllegalArgumentException e) {
            log.error("Invalid item type: {}", type, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<WorkItemDetailDTO>builder()
                            .success(false)
                            .message(ResponseCodes.ERR_INVALID_ITEM_TYPE.name())
                            .build());
        } catch (Exception e) {
            log.error("Error retrieving work order detail for phone number: {}, work item id: {}",
                    phoneNumber, workItemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<WorkItemDetailDTO>builder()
                            .success(false)
                            .message(ResponseCodes.ERR_FAILED_TO_RETRIEVE_WORK_ORDER_DETAIL.name())
                            .build());
        }
    }

    @GetMapping("/{workItemId}/tasks")
    public ResponseEntity<ApiResponse<WorkItemTasksResponseDTO>> getWorkItemTasks(
            @PathVariable UUID workItemId,
            Authentication authentication) {

        try {
            // Extract tenant ID from JWT token (validates token and tenant ID presence)
            UUID tenantId = requestTokenUtil.getTenantIdFromToken();
            WorkItemTasksResponseDTO tasksResponse = workItemService.getWorkItemTasks(workItemId, tenantId);

            return ResponseEntity.ok(ApiResponse.<WorkItemTasksResponseDTO>builder()
                    .success(true)
                    .data(tasksResponse)
                    .message(ResponseCodes.TASKS_RETRIEVAL_SUCCESSFUL.name())
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving tasks for work item id: {}", workItemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<WorkItemTasksResponseDTO>builder()
                            .success(false)
                            .message(ResponseCodes.ERR_FAILED_TO_RETRIEVE_TASKS.name())
                            .build());
        }
    }

    @PostMapping("/log-hours")
    public ResponseEntity<ApiResponse<Object>> logHours(
            @Valid @RequestBody LogHoursRequest request,
            Authentication authentication) {

        try {
            // Extract tenant ID from JWT token (validates token and tenant ID presence)
            UUID tenantId = requestTokenUtil.getTenantIdFromToken();
            HttpClientResponse<Object> response = workItemService.logHours(request, tenantId);

            // Return the response from labour service
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.<Object>builder()
                                .success(true)
                                .data(response.getBody())
                                .message(ResponseCodes.HOURS_LOGGED_SUCCESSFUL.name())
                                .build());
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.<Object>builder()
                                .success(false)
                                .data(response.getBody())
                                .message(ResponseCodes.ERR_FAILED_TO_LOG_HOURS.name())
                                .build());
            }
        } catch (Exception e) {
            log.error("Error logging hours for work item id: {}, user id: {}",
                    request.getWorkItemId(), request.getUserId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Object>builder()
                            .success(false)
                            .message(ResponseCodes.ERR_FAILED_TO_LOG_HOURS.name())
                            .build());
        }
    }

    @PostMapping("/save-asset-rating")
    public ResponseEntity<ApiResponse<Object>> saveAssetRating(
            @Valid @RequestBody SaveAssetRatingRequest request,
            Authentication authentication) {

        try {
            // Extract tenant ID from JWT token (validates token and tenant ID presence)
            UUID tenantId = requestTokenUtil.getTenantIdFromToken();
            HttpClientResponse<Object> response = workItemService.saveAssetRating(request, tenantId);

            // Return the response from work order service
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.<Object>builder()
                                .success(true)
                                .data(response.getBody())
                                .message(ResponseCodes.ASSET_RATING_SAVED_SUCCESSFUL.name())
                                .build());
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.<Object>builder()
                                .success(false)
                                .data(response.getBody())
                                .message(ResponseCodes.ERR_FAILED_TO_SAVE_ASSET_RATING.name())
                                .build());
            }
        } catch (Exception e) {
            log.error("Error saving asset rating for work order id: {}, user id: {}, asset id: {}",
                    request.getWorkOrderId(), request.getUserId(), request.getAssetId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Object>builder()
                            .success(false)
                            .message(ResponseCodes.ERR_FAILED_TO_SAVE_ASSET_RATING.name())
                            .build());
        }
    }

    @PostMapping("/{workItemId}/add-comment")
    public ResponseEntity<ApiResponse<Object>> addComment(
            @PathVariable UUID workItemId,
            @RequestParam("type") WorkItemType type,
            @Valid @RequestBody AddCommentRequest request,
            Authentication authentication) {

        try {
            // Extract tenant ID from JWT token (validates token and tenant ID presence)
            UUID tenantId = requestTokenUtil.getTenantIdFromToken();
            HttpClientResponse<Object> response = workItemService.addComment(workItemId, request, type, tenantId);

            // Return the response from work order service
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.<Object>builder()
                                .success(true)
                                .data(response.getBody())
                                .message(ResponseCodes.COMMENT_ADDED_SUCCESSFUL.name())
                                .build());
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.<Object>builder()
                                .success(false)
                                .data(response.getBody())
                                .message(ResponseCodes.ERR_FAILED_TO_ADD_COMMENT.name())
                                .build());
            }
        } catch (Exception e) {
            log.error("Error adding comment for work item id: {}",
                    workItemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Object>builder()
                            .success(false)
                            .message(ResponseCodes.ERR_FAILED_TO_ADD_COMMENT.name())
                            .build());
        }
    }

    @PostMapping("/add-comment")
    public ResponseEntity<ApiResponse<Object>> addComment(
            @RequestParam("commentId") UUID commentId,
            @RequestParam("workItemId") UUID workItemId,
            @RequestParam("type") WorkItemType type,
            Authentication authentication) {

        try {
            // Extract tenant ID from JWT token (validates token and tenant ID presence)
            UUID tenantId = requestTokenUtil.getTenantIdFromToken();

            log.info("Adding comment {} to work item {} of type {} for tenant {}",
                    commentId, workItemId, type, tenantId);

            HttpClientResponse<Object> response = workItemService.addCommentToWorkItem(workItemId, commentId, type);

            // Return the response from work order service
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.<Object>builder()
                                .success(true)
                                .data(response.getBody())
                                .message(ResponseCodes.COMMENT_ADDED_SUCCESSFUL.name())
                                .build());
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.<Object>builder()
                                .success(false)
                                .data(response.getBody())
                                .message(ResponseCodes.ERR_FAILED_TO_ADD_COMMENT.name())
                                .build());
            }
        } catch (Exception e) {
            log.error("Error adding comment {} to work item {} of type {}: {}",
                    commentId, workItemId, type, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Object>builder()
                            .success(false)
                            .message(ResponseCodes.ERR_FAILED_TO_ADD_COMMENT.name())
                            .build());
        }
    }

}

