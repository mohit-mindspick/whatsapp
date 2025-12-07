package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.common.constant.ErrorMessages;
import com.assetneuron.whatsapp.enums.ChecklistItemStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveTaskChecklistItemRequest {

    @JsonProperty(value = "id")
    @NotNull(message = ErrorMessages.VALIDATION_CHECKLIST_ITEM_ID_REQUIRED)
    private UUID id;

    @JsonProperty(value = "response")
    private String response;

    @JsonProperty(value = "status")
    private ChecklistItemStatus status;

    @JsonProperty(value = "comment_id")
    @NotNull(message = ErrorMessages.VALIDATION_CHECKLIST_ITEM_ID_REQUIRED)
    private UUID commentId;

}

