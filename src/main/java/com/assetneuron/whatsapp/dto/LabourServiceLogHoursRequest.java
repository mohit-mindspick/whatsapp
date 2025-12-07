package com.assetneuron.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for logging hours in the labour service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabourServiceLogHoursRequest {
    
    @JsonProperty(value = "user_id")
    private UUID userId;
    
    @JsonProperty(value = "hours_logged")
    private BigDecimal hoursLogged;
    
    @JsonProperty(value = "workOrderCode")
    private String workOrderCode;
}

