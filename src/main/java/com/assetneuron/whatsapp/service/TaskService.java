package com.assetneuron.whatsapp.service;

import com.assetneuron.whatsapp.common.adaptor.HttpClientAdapter;
import com.assetneuron.whatsapp.common.adaptor.HttpClientResponse;
import com.assetneuron.whatsapp.config.ReadOnly;
import com.assetneuron.whatsapp.dto.AddCommentRequest;
import com.assetneuron.whatsapp.dto.ChecklistItemDTO;
import com.assetneuron.whatsapp.dto.SaveTaskChecklistItemsRequest;
import com.assetneuron.whatsapp.dto.SaveTaskChecklistItemRequest;
import com.assetneuron.whatsapp.dto.TaskChecklistResponseDTO;
import com.assetneuron.whatsapp.dto.WorkOrderServiceSaveChecklistItemsRequest;
import com.assetneuron.whatsapp.enums.WorkItemType;
import com.assetneuron.whatsapp.model.ChecklistItem;
import com.assetneuron.whatsapp.model.Task;
import com.assetneuron.whatsapp.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final HttpClientAdapter httpClientAdapter;
    private final WorkOrderService workOrderService;

    @Value("${workorder.service.url:http://localhost:8080}")
    private String workOrderServiceUrl;

    @ReadOnly
    public TaskChecklistResponseDTO getTaskChecklistItems(UUID workOrderId, UUID taskId, UUID tenantId) {
        log.info("Getting checklist items for work order id: {}, task id: {}, tenant id: {}",
                workOrderId, taskId, tenantId);

        Task task = taskRepository.findByIdAndWorkOrderIdAndTenantId(taskId, workOrderId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Task not found with ID: " + taskId + " for work order: " + workOrderId));

        List<ChecklistItemDTO> checklistItemDTOs = new ArrayList<>();

        if (task.getChecklistItems() != null && !task.getChecklistItems().isEmpty()) {
            checklistItemDTOs = task.getChecklistItems().stream()
                    .map(this::mapChecklistItemToDto)
                    .collect(Collectors.toList());
        }

        return TaskChecklistResponseDTO.builder()
                .id(task.getId())
                .sequence(task.getSequence())
                .countOfChecklist(checklistItemDTOs.size())
                .checklistItems(checklistItemDTOs)
                .build();
    }

    public HttpClientResponse<Object> saveChecklistItemResponse(SaveTaskChecklistItemsRequest request, UUID tenantId) {
        log.info("Saving checklist item responses for work order id: {}, task id: {}, tenant id: {}, items count: {}",
                request.getWorkOrderId(), request.getTaskId(), tenantId,
                request.getChecklistItems() != null ? request.getChecklistItems().size() : 0);

        // Step 1: Get checklist items from request and map to work order service format
        List<WorkOrderServiceSaveChecklistItemsRequest.ChecklistResponseItem> checklistResponseItems = 
                request.getChecklistItems() != null && !request.getChecklistItems().isEmpty()
                        ? request.getChecklistItems().stream()
                                .map(item -> WorkOrderServiceSaveChecklistItemsRequest.ChecklistResponseItem.builder()
                                        .id(item.getId())
                                        .response(item.getResponse())
                                        .status(item.getStatus())
                                        .commentId(item.getCommentId()) // Will be set after creating comment
                                        .build())
                                .collect(Collectors.toList())
                        : Collections.emptyList();

        // Step 2: First save the response (response, status) in work order service
        WorkOrderServiceSaveChecklistItemsRequest workOrderRequest = WorkOrderServiceSaveChecklistItemsRequest.builder()
                .workOrderId(request.getWorkOrderId())
                .taskId(request.getTaskId())
                .checklistItems(checklistResponseItems)
                .build();

        String url = workOrderServiceUrl + "/api/v1/workorders/" + request.getWorkOrderId() + "/tasks/" + request.getTaskId() + "/checklist-items/response";
        HttpClientResponse<Object> response = httpClientAdapter.put(url, workOrderRequest, Object.class);

        if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
            log.error("Failed to save checklist item responses - Work Order ID: {}, Task ID: {}, Status: {}",
                    request.getWorkOrderId(), request.getTaskId(), response.getStatusCode());
            return response;
        }

        log.info("Checklist item responses saved successfully - Work Order ID: {}, Task ID: {}, Items Count: {}",
                request.getWorkOrderId(), request.getTaskId(),
                request.getChecklistItems() != null ? request.getChecklistItems().size() : 0);

        return response;
    }

    private ChecklistItemDTO mapChecklistItemToDto(ChecklistItem checklistItem) {
        return ChecklistItemDTO.builder()
                .id(checklistItem.getId())
                .itemText(checklistItem.getItemText())
                .type(checklistItem.getType())
                .possibleOptions(checklistItem.getPossibleOptions())
                .build();
    }

}

