package com.assetneuron.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for adding comment to work order in the work order service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderServiceAddCommentRequest {
    
    @JsonProperty(value = "workorder_id")
    private UUID workOrderId;
    
    @JsonProperty(value = "comment_id")
    private UUID commentId;
}

