package com.assetneuron.whatsapp.dto;

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
 * Request DTO for updating work order parts in the work order service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderServiceUpdatePartsRequest {

    @NotNull(message = "Work order ID is required")
    @JsonProperty(value = "work_order_id")
    private UUID workOrderId;

    @Valid
    @NotEmpty(message = "Work order parts list cannot be empty")
    @JsonProperty(value = "parts")
    private List<WorkOrderServiceUpdateParts> parts;

}

