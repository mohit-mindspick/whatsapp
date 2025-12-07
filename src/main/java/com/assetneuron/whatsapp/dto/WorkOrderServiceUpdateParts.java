package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.enums.WorkOrderPartStatus;
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
public class WorkOrderServiceUpdateParts {

    @NotNull(message = "Work order part ID is required")
    @JsonProperty(value = "id")
    private UUID id;

    @JsonProperty(value = "part_id")
    private UUID partId;

    @Positive(message = "Quantity must be positive")
    @JsonProperty(value = "quantity")
    private Integer quantity;

    @JsonProperty(value = "status")
    private WorkOrderPartStatus status;

}
