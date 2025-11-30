package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.common.constant.ErrorMessages;
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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveTaskChecklistItemsRequest {

    @JsonProperty(value = "work_order_id")
    @NotNull(message = ErrorMessages.VALIDATION_WORK_ORDER_ID_REQUIRED)
    private UUID workOrderId;
    
    @JsonProperty(value = "task_id")
    @NotNull(message = ErrorMessages.VALIDATION_TASK_ID_REQUIRED)
    private UUID taskId;

    @JsonProperty(value = "checklist_items")
    @NotEmpty(message = ErrorMessages.VALIDATION_CHECKLIST_ITEMS_REQUIRED)
    @Valid
    private List<SaveTaskChecklistItemRequest> checklistItems;
}

