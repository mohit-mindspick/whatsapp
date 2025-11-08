package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.common.constant.ErrorMessages;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewMyWorkRequest {
    
    @JsonProperty(value = "phone_number")
    @NotBlank(message = ErrorMessages.VALIDATION_PHONE_NUMBER_REQUIRED)
    private String phoneNumber;
    
    @JsonProperty(value = "start_date")
    @NotNull(message = ErrorMessages.VALIDATION_START_DATE_REQUIRED)
    private LocalDateTime startDate;
    
    @JsonProperty(value = "end_date")
    @NotNull(message = ErrorMessages.VALIDATION_END_DATE_REQUIRED)
    private LocalDateTime endDate;
}

