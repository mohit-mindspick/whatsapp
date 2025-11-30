package com.assetneuron.whatsapp.service;

import com.assetneuron.whatsapp.common.adaptor.HttpClientAdapter;
import com.assetneuron.whatsapp.common.adaptor.HttpClientResponse;
import com.assetneuron.whatsapp.config.ReadOnly;
import com.assetneuron.whatsapp.dto.AddCommentRequest;
import com.assetneuron.whatsapp.dto.CommentServiceCreateRequest;
import com.assetneuron.whatsapp.dto.CommentServiceCreateResponse;
import com.assetneuron.whatsapp.dto.DocumentServiceCreateRequest;
import com.assetneuron.whatsapp.dto.DocumentServiceCreateResponse;
import com.assetneuron.whatsapp.dto.LabourServiceLogHoursRequest;
import com.assetneuron.whatsapp.dto.LogHoursRequest;
import com.assetneuron.whatsapp.dto.MyWorkDTO;
import com.assetneuron.whatsapp.dto.SaveAssetRatingRequest;
import com.assetneuron.whatsapp.dto.TaskDTO;
import com.assetneuron.whatsapp.dto.ViewMyWorkRequest;
import com.assetneuron.whatsapp.dto.ViewWorkOrderDetailRequest;
import com.assetneuron.whatsapp.dto.WorkItemDetailDTO;
import com.assetneuron.whatsapp.dto.WorkItemTasksResponseDTO;
import com.assetneuron.whatsapp.dto.WorkOrderServiceAddCommentRequest;
import com.assetneuron.whatsapp.dto.WorkOrderServiceSaveAssetRatingRequest;
import com.assetneuron.whatsapp.enums.DateFilterType;
import com.assetneuron.whatsapp.enums.WorkItemDetailType;
import com.assetneuron.whatsapp.enums.WorkItemType;
import com.assetneuron.whatsapp.enums.WorkOrderType;
import com.assetneuron.whatsapp.model.Cases;
import com.assetneuron.whatsapp.model.Task;
import com.assetneuron.whatsapp.model.WorkOrder;
import com.assetneuron.whatsapp.repository.CaseRepository;
import com.assetneuron.whatsapp.repository.TaskRepository;
import com.assetneuron.whatsapp.repository.WorkOrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final CaseRepository caseRepository;
    private final TaskRepository taskRepository;
    private final HttpClientAdapter httpClientAdapter;
    private final ObjectMapper objectMapper;

    @Value("${workorder.service.url:http://localhost:8080}")
    private String workOrderServiceUrl;

    @Value("${document.service.url:http://localhost:8080}")
    private String documentServiceUrl;

    @Value("${comment.service.url:http://localhost:8080}")
    private String commentServiceUrl;

    @ReadOnly
    public List<MyWorkDTO> viewMyWork(ViewMyWorkRequest request, UUID tenantId) {

        LocalDate dueDate = calculateDueDateFromFilter(request.getDateFilter());

        log.info("Getting work orders and cases for phone number: {} with date filter: {} (due date: {}) for tenant: {}",
                request.getPhoneNumber(), request.getDateFilter(), dueDate, tenantId);

        List<Object[]> results = workOrderRepository.findMyWorkByUserPhoneNumberAndDueDate(
                request.getPhoneNumber(),
                dueDate,
                tenantId);

        List<MyWorkDTO> myWorkOrders = new ArrayList<>();

        for (Object[] result : results) {
            UUID id = (UUID) result[0];
            String title = (String) result[1];
            String code = (String) result[2];
            String importance = (String) result[3];
            String type = (String) result[4];

            myWorkOrders.add(MyWorkDTO.builder()
                    .id(id)
                    .name(title)
                    .code(code)
                    .priority(importance)
                    .type(type)
                    .build());
        }

        log.info("Found {} work items for phone number: {}",
                myWorkOrders.size(), request.getPhoneNumber());

        return myWorkOrders;
    }

    @ReadOnly
    public WorkItemDetailDTO getWorkItemDetail(ViewWorkOrderDetailRequest request, UUID tenantId) {
        log.info("Getting work item detail for phone number: {}, work item id: {}, item type: {}",
                request.getPhoneNumber(), request.getWorkItemId(), request.getItemType());

        if (WorkItemType.WORKORDER == request.getItemType()) {
            WorkOrder workOrder = workOrderRepository.findByIdAndTenantId(request.getWorkItemId(), tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("Work Order not found with id: " + request.getWorkItemId()));

            // Map WorkOrderType to WorkItemDetailType
            WorkItemDetailType detailType = WorkItemDetailType.CORRECTIVE;
            if (workOrder.getType() == WorkOrderType.PREVENTIVE) {
                detailType = WorkItemDetailType.PREVENTIVE;
            }

            // Get task count
            int taskCount = workOrder.getTasks() != null ? workOrder.getTasks().size() : 0;

            return WorkItemDetailDTO.builder()
                    .workItemId(workOrder.getId())
                    .workItemName(workOrder.getTitle())
                    .priority(workOrder.getPriority() != null ? workOrder.getPriority().getName() : null)
                    .category(workOrder.getType() != null ? workOrder.getType().getLabel() : null)
                    .assetId(workOrder.getAssetId())
                    .assetName(workOrder.getAssetName())
                    .locationId(workOrder.getLocationId() != null ? UUID.fromString(workOrder.getLocationId()) : null)
                    .locationName(workOrder.getLocationName())
                    .type(detailType)
                    .taskCount(taskCount)
                    .dueDate(workOrder.getDueDate())
                    .status(workOrder.getStatus() != null ? workOrder.getStatus().name() : null)
                    .build();

        } else if (WorkItemType.CASE == request.getItemType()) {
            Cases cases = caseRepository.findByIdAndTenantId(request.getWorkItemId(), tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("Case not found with id: " + request.getWorkItemId()));

            return WorkItemDetailDTO.builder()
                    .workItemId(cases.getId())
                    .workItemName(cases.getTitle())
                    .priority(cases.getSeverity() != null ? cases.getSeverity().name() : null)
                    .category("CASE")
                    .assetId(cases.getAssetId() != null ? UUID.fromString(cases.getAssetId()) : null)
                    .assetName(cases.getAssetName())
                    .locationName(cases.getLocation())
                    .taskCount(0)
                    .status(null)
                    .build();

        } else {
            throw new IllegalArgumentException("Invalid item type: " + request.getItemType());
        }
    }

    @ReadOnly
    public WorkItemTasksResponseDTO getWorkItemTasks(UUID workItemId, UUID tenantId) {
        log.info("Getting tasks for work item id: {}, tenant id:", workItemId, tenantId);

        List<Task> tasks = taskRepository.findByWorkOrderIdAndTenantId(workItemId, tenantId);
        if (tasks == null || tasks.isEmpty()) {

            return WorkItemTasksResponseDTO.builder()
                    .tasks(List.of())
                    .totalTask(0)
                    .build();
        } else {
            List<TaskDTO> taskDTOList = tasks.stream()
                    .map(this::mapTaskToDto)
                    .collect(Collectors.toList());

            return WorkItemTasksResponseDTO.builder()
                    .tasks(taskDTOList)
                    .totalTask(taskDTOList.size())
                    .build();
        }
    }

    public HttpClientResponse<Object> logHours(LogHoursRequest request, UUID tenantId) {
        log.info("Logging hours for work item id: {}, user id: {}, time in hours: {} hours, tenant id: {}",
                request.getWorkItemId(), request.getUserId(), request.getTimeInHours(), tenantId);

        // Fetch work order to get the code
        WorkOrder workOrder = workOrderRepository.findById(request.getWorkItemId())
                .orElseThrow(() -> new RuntimeException("Work Order not found with id: " + request.getWorkItemId()));

        // Build request payload for labour service
        LabourServiceLogHoursRequest labourRequest = LabourServiceLogHoursRequest.builder()
                .userId(request.getUserId())
                .hoursLogged(BigDecimal.valueOf(request.getTimeInHours()))
                .workOrderCode(workOrder.getCode())
                .build();

        // Construct URL for work order service
        String url = workOrderServiceUrl + "/api/v1/workorders/" + workOrder.getCode() + "/log-hours";

        // Make HTTP call to work order service (X-Tenant-Id is automatically included by HttpClientAdapter)
        HttpClientResponse<Object> response = httpClientAdapter.post(url, labourRequest, Object.class);

        log.info("Hours logged successfully - Work Item ID: {}, User ID: {}, Work Order Code: {}, Response Status: {}",
                request.getWorkItemId(), request.getUserId(), workOrder.getCode(), response.getStatusCode());

        return response;
    }

    public HttpClientResponse<Object> saveAssetRating(SaveAssetRatingRequest request, UUID tenantId) {
        log.info("Saving asset rating for work order id: {}, user id: {}, asset id: {}, rating: {}, tenant id: {}",
                request.getWorkOrderId(), request.getUserId(), request.getAssetId(), request.getRating(), tenantId);

        // Build request payload for work order service
        WorkOrderServiceSaveAssetRatingRequest workOrderRequest = WorkOrderServiceSaveAssetRatingRequest.builder()
                .workOrderId(request.getWorkOrderId())
                .userId(request.getUserId())
                .assetId(request.getAssetId())
                .rating(request.getRating())
                .build();

        // Construct URL for work order service
        String url = workOrderServiceUrl + "/api/v1/workorders/asset-rating";

        // Make HTTP call to work order service (X-Tenant-Id is automatically included by HttpClientAdapter)
        HttpClientResponse<Object> response = httpClientAdapter.post(url, workOrderRequest, Object.class);

        log.info("Asset rating saved successfully - Work Order ID: {}, User ID: {}, Asset ID: {}, Rating: {}, Response Status: {}",
                request.getWorkOrderId(), request.getUserId(), request.getAssetId(), request.getRating(), response.getStatusCode());

        return response;
    }

    public HttpClientResponse<Object> addComment(UUID workItemId, AddCommentRequest request, WorkItemType type, UUID tenantId) {
        log.info("Adding comment for work order id: {}, tenant id: {}, documents count: {}",
                workItemId, tenantId,
                request.getDocuments() != null ? request.getDocuments().size() : 0);

        // Step 1: Create documents from URLs and get document UUIDs
        List<UUID> documentIds = createDocumentsFromUrls(request.getDocuments(), workItemId, type);

        // Step 2: Create comment with content and document IDs, get comment UUID
        UUID commentId = createComment(request.getContent(), documentIds, workItemId, type);

        // Step 3: Add comment
        if (WorkItemType.WORKORDER == type) {
            return addCommentToWorkOrder(workItemId, commentId);
        } else if (WorkItemType.CASE == type) {
            return addCommentToWorkOrder(workItemId, commentId);
        } else {
            throw new IllegalArgumentException("Invalid item type: " + type);
        }
    }

    /**
     * Step 1: Create documents from URLs by calling document service
     *
     * @param documents  List of document objects with URL, name, and mimeType
     * @param workItemId Work item ID (work order or case ID) to use as parent
     * @param type       Work item type (WORKORDER or CASE) to use as parent type
     * @return List of document UUIDs
     */
    private List<UUID> createDocumentsFromUrls(List<AddCommentRequest.ExternalDocument> documents, UUID workItemId, WorkItemType type) {
        if (documents == null || documents.isEmpty()) {
            log.info("No documents to create, skipping document service call");
            return Collections.emptyList();
        }

        log.info("Creating documents from URLs - Count: {}, Parent ID: {}, Parent Type: {}", 
                documents.size(), workItemId, type);

        // Map AddCommentRequest.ExternalDocument to DocumentServiceCreateRequest.ExternalDocument
        List<DocumentServiceCreateRequest.ExternalDocument> documentList = documents.stream()
                .map(doc -> DocumentServiceCreateRequest.ExternalDocument.builder()
                        .url(doc.getUrl())
                        .name(doc.getName())
                        .mimeType(doc.getMimeType())
                        .parentType(type.name()) // Use WorkItemType enum name (WORKORDER or CASE)
                        .parentId(workItemId)
                        .build())
                .collect(Collectors.toList());

        // Build request payload with list of documents
        DocumentServiceCreateRequest documentRequest = DocumentServiceCreateRequest.builder()
                .documents(documentList)
                .build();

        String documentServiceUrl = this.documentServiceUrl + "/api/v1/documents/import";
        HttpClientResponse<Object> documentResponse = httpClientAdapter.post(documentServiceUrl, documentRequest, Object.class);

        if (documentResponse.getStatusCode() >= 200 && documentResponse.getStatusCode() < 300) {
            try {
                DocumentServiceCreateResponse documentServiceResponse = parseDocumentServiceResponse(documentResponse.getBody());
                // Extract UUIDs from the documents array
                List<UUID> documentIds = documentServiceResponse.getDocuments() != null
                        ? documentServiceResponse.getDocuments().stream()
                                .map(DocumentServiceCreateResponse.DocumentInfo::getUuid)
                                .filter(uuid -> uuid != null)
                                .collect(Collectors.toList())
                        : Collections.emptyList();
                log.info("Created {} documents successfully", documentIds.size());
                return documentIds;
            } catch (Exception e) {
                log.error("Failed to parse document service response: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to parse document service response: " + e.getMessage(), e);
            }
        } else {
            log.error("Document service returned error status: {}", documentResponse.getStatusCode());
            throw new RuntimeException("Failed to create documents. Status: " + documentResponse.getStatusCode());
        }
    }

    /**
     * Step 2: Create comment with content and document IDs by calling comment service
     *
     * @param content     Comment content
     * @param documentIds List of document UUIDs
     * @param workItemId  Work item ID (work order or case ID) to use as parent
     * @param type        Work item type (WORKORDER or CASE) to use as parent type
     * @return Comment UUID
     */
    private UUID createComment(String content, List<UUID> documentIds, UUID workItemId, WorkItemType type) {
        log.info("Creating comment with content, {} document IDs, Parent ID: {}, Parent Type: {}", 
                documentIds.size(), workItemId, type);

        // Convert document UUIDs to String list for the request
        List<String> documentList = documentIds != null && !documentIds.isEmpty()
                ? documentIds.stream()
                        .map(UUID::toString)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        // Build request payload with all required fields
        CommentServiceCreateRequest commentRequest = CommentServiceCreateRequest.builder()
                .content(content)
                .parentId(workItemId.toString())
                .parentType(type.name()) // Use WorkItemType enum name (WORKORDER or CASE)
                .sourceService("whatsapp") // Service name that is creating the comment
                .documentList(documentList)
                .build();

        String commentServiceUrl = this.commentServiceUrl + "/api/v1/comments";
        HttpClientResponse<Object> commentResponse = httpClientAdapter.post(commentServiceUrl, commentRequest, Object.class);

        if (commentResponse.getStatusCode() >= 200 && commentResponse.getStatusCode() < 300) {
            try {
                CommentServiceCreateResponse commentServiceResponse = parseCommentServiceResponse(commentResponse.getBody());
                UUID commentId = commentServiceResponse.getId();
                if (commentId == null) {
                    throw new RuntimeException("Comment service did not return a comment ID");
                }
                log.info("Created comment with ID: {}", commentId);
                return commentId;
            } catch (Exception e) {
                log.error("Failed to parse comment service response: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to parse comment service response: " + e.getMessage(), e);
            }
        } else {
            log.error("Comment service returned error status: {}", commentResponse.getStatusCode());
            throw new RuntimeException("Failed to create comment. Status: " + commentResponse.getStatusCode());
        }
    }

    /**
     * Add an existing comment to a work item (work order or case)
     *
     * @param workItemId Work item ID (work order or case ID)
     * @param commentId  Comment ID
     * @param type      Work item type (WORKORDER or CASE)
     * @return HTTP response from work order service
     */
    public HttpClientResponse<Object> addCommentToWorkItem(UUID workItemId, UUID commentId, WorkItemType type) {
        log.info("Adding comment {} to work item {} of type {}", commentId, workItemId, type);

        if (WorkItemType.WORKORDER == type) {
            return addCommentToWorkOrder(workItemId, commentId);
        } else if (WorkItemType.CASE == type) {
            return addCommentToCase(workItemId, commentId);
        } else {
            throw new IllegalArgumentException("Invalid item type: " + type);
        }
    }

    /**
     * Step 3: Add comment to work order by calling work order service
     *
     * @param workOrderId Work order ID
     * @param commentId   Comment ID
     * @return HTTP response from work order service
     */
    private HttpClientResponse<Object> addCommentToWorkOrder(UUID workOrderId, UUID commentId) {
        log.info("Adding comment {} to work order {}", commentId, workOrderId);

        WorkOrderServiceAddCommentRequest workOrderRequest = WorkOrderServiceAddCommentRequest.builder()
                .workOrderId(workOrderId)
                .commentId(commentId)
                .build();

        String workOrderCommentUrl = workOrderServiceUrl + "/api/v1/workorders/" + workOrderId + "/comments";
        HttpClientResponse<Object> workOrderResponse = httpClientAdapter.post(workOrderCommentUrl, workOrderRequest, Object.class);

        log.info("Comment added to work order - Work Order ID: {}, Comment ID: {}, Response Status: {}",
                workOrderId, commentId, workOrderResponse.getStatusCode());

        return workOrderResponse;
    }

    /**
     * Step 3: Add comment to case by calling case service
     *
     * @param caseId    Case ID
     * @param commentId Comment ID
     * @return HTTP response from work order service
     */
    private HttpClientResponse<Object> addCommentToCase(UUID caseId, UUID commentId) {
        log.info("Adding comment {} to case {}", commentId, caseId);

        WorkOrderServiceAddCommentRequest caseRequest = WorkOrderServiceAddCommentRequest.builder()
                .workOrderId(caseId)
                .commentId(commentId)
                .build();

        String caseCommentUrl = workOrderServiceUrl + "/api/v1/workorders/" + caseId + "/comments";
        HttpClientResponse<Object> caseResponse = httpClientAdapter.post(caseCommentUrl, caseRequest, Object.class);

        log.info("Comment added to case - Case ID: {}, Comment ID: {}, Response Status: {}",
                caseId, commentId, caseResponse.getStatusCode());

        return caseResponse;
    }

    /**
     * Parse document service response, handling both wrapped and direct responses
     *
     * @param responseBody Response body from document service
     * @return DocumentServiceCreateResponse
     */
    private DocumentServiceCreateResponse parseDocumentServiceResponse(Object responseBody) {
        if (responseBody instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> responseMap = (java.util.Map<String, Object>) responseBody;
            // Check if wrapped in ApiResponse with data field
            if (responseMap.containsKey("data")) {
                return objectMapper.convertValue(
                        responseMap.get("data"), DocumentServiceCreateResponse.class);
            } else {
                return objectMapper.convertValue(
                        responseBody, DocumentServiceCreateResponse.class);
            }
        } else {
            return objectMapper.convertValue(
                    responseBody, DocumentServiceCreateResponse.class);
        }
    }

    /**
     * Parse comment service response, handling both wrapped and direct responses
     *
     * @param responseBody Response body from comment service
     * @return CommentServiceCreateResponse
     */
    private CommentServiceCreateResponse parseCommentServiceResponse(Object responseBody) {
        if (responseBody instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> responseMap = (java.util.Map<String, Object>) responseBody;
            // Check if wrapped in ApiResponse with data field
            if (responseMap.containsKey("data")) {
                return objectMapper.convertValue(
                        responseMap.get("data"), CommentServiceCreateResponse.class);
            } else {
                return objectMapper.convertValue(
                        responseBody, CommentServiceCreateResponse.class);
            }
        } else {
            return objectMapper.convertValue(
                    responseBody, CommentServiceCreateResponse.class);
        }
    }

    private TaskDTO mapTaskToDto(Task task) {
        TaskDTO.TaskDTOBuilder builder = TaskDTO.builder()
                .taskId(task.getId())
                .sequence(task.getSequence())
                .name(task.getName())
                .instruction(task.getInstructions())
                .duration(task.getDurationValue());

        return builder.build();
    }

    /**
     * Calculate due date based on date filter type
     *
     * @param dateFilter The date filter type (TODAY, TOMORROW, WEEK)
     * @return LocalDate calculated based on the filter type
     */
    private LocalDate calculateDueDateFromFilter(DateFilterType dateFilter) {
        LocalDate today = LocalDate.now();

        switch (dateFilter) {
            case TODAY:
                return today;
            case TOMORROW:
                return today.plusDays(1);
            case WEEK:
                return today.plusDays(7);
            default:
                log.warn("Unknown date filter type: {}, defaulting to TODAY", dateFilter);
                return today;
        }
    }
}

