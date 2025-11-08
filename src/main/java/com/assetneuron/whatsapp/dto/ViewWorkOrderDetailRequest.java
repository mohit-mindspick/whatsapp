package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.common.constant.ErrorMessages;
import com.assetneuron.whatsapp.enums.WorkItemType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
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
public class ViewWorkOrderDetailRequest {
    
    @JsonProperty(value = "phone_number")
    @NotBlank(message = ErrorMessages.VALIDATION_PHONE_NUMBER_REQUIRED)
    private String phoneNumber;
    
    @JsonProperty(value = "work_item_id")
    @NotNull(message = ErrorMessages.VALIDATION_WORK_ITEM_ID_REQUIRED_LOWER)
    private UUID workItemId;
    
    @JsonProperty(value = "item_type")
    @NotNull(message = ErrorMessages.VALIDATION_ITEM_TYPE_REQUIRED)
    private WorkItemType itemType;
}

