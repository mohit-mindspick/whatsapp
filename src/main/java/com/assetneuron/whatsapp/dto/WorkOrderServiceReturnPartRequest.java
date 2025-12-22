package com.assetneuron.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for returning part in the work order service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderServiceReturnPartRequest {
    
    @JsonProperty(value = "workOrderId")
    private UUID workOrderId;
    
    @JsonProperty(value = "partId")
    private UUID partId;
    
    @JsonProperty(value = "partName")
    private String partName;
    
    @JsonProperty(value = "quantity")
    private BigDecimal quantity;
}

