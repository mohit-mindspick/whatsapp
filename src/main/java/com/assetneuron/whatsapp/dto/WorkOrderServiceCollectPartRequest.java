package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.enums.WorkOrderPartStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for collecting part in the work order service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderServiceCollectPartRequest {
    
    @JsonProperty(value = "workOrderId")
    private UUID workOrderId;
    
    @JsonProperty(value = "partId")
    private UUID partId;
    
    @JsonProperty(value = "partStatus")
    private WorkOrderPartStatus partStatus;
}

