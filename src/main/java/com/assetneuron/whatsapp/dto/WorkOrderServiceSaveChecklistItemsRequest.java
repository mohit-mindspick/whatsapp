package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.enums.ChecklistItemStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for saving checklist items in the work order service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderServiceSaveChecklistItemsRequest {

    @JsonProperty(value = "workorder_id")
    private UUID workOrderId;

    @JsonProperty(value = "task_id")
    private UUID taskId;

    @NotEmpty(message = "Checklist items list cannot be empty")
    @Valid
    @JsonProperty(value = "checklist_items")
    private List<WorkOrderServiceSaveChecklistItemsRequest.ChecklistResponseItem> checklistItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChecklistResponseItem {

        @NotNull(message = "Checklist item ID is required")
        @JsonProperty(value = "id")
        private UUID id;

        @JsonProperty(value = "comment_id")
        private UUID commentId;

        @JsonProperty(value = "response")
        private String response;

        @JsonProperty(value = "status")
        private ChecklistItemStatus status;

    }

}

