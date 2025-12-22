package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.common.constant.ErrorMessages;
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
public class WorkOrderPartDTO {

    @JsonProperty(value = "work_order_part_id")
    @NotNull(message = ErrorMessages.VALIDATION_WORK_ORDER_PART_ID_REQUIRED)
    private UUID id;

    @JsonProperty(value = "part_id")
    @NotNull(message = ErrorMessages.VALIDATION_PART_ID_REQUIRED)
    private UUID partId;

    @JsonProperty(value = "quantity")
    @NotNull(message = ErrorMessages.VALIDATION_QUANTITY_REQUIRED)
    @DecimalMin(value = "0.001", message = ErrorMessages.VALIDATION_QUANTITY_POSITIVE)
    private BigDecimal quantity;

    private WorkOrderPartStatus status;

}
