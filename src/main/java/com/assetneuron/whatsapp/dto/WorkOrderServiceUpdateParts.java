package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.enums.WorkOrderPartStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderServiceUpdateParts {

    @NotNull(message = "Work order part ID is required")
    @JsonProperty(value = "id")
    private UUID id;

    @JsonProperty(value = "part_id")
    private UUID partId;

    @DecimalMin(value = "0.001", message = "Quantity must be positive")
    @JsonProperty(value = "quantity")
    private BigDecimal quantity;

    @JsonProperty(value = "status")
    private WorkOrderPartStatus status;

}
