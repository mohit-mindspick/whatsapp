package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.common.constant.ErrorMessages;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogHoursRequest {
    
    @JsonProperty(value = "work_item_id")
    @NotNull(message = ErrorMessages.VALIDATION_WORK_ITEM_ID_REQUIRED)
    private UUID workItemId;
    
    @JsonProperty(value = "user_id")
    @NotNull(message = ErrorMessages.VALIDATION_USER_ID_REQUIRED)
    private UUID userId;
    
    @JsonProperty(value = "time_in_hours")
    @NotNull(message = ErrorMessages.VALIDATION_TIME_IN_HOURS_REQUIRED)
    @Positive(message = ErrorMessages.VALIDATION_TIME_IN_HOURS_POSITIVE)
    private Double timeInHours;
}

