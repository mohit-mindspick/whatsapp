package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.common.constant.ErrorMessages;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
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
public class ReturnPartRequest {
    
    @JsonProperty(value = "work_order_id")
    @NotNull(message = ErrorMessages.VALIDATION_WORK_ORDER_ID_REQUIRED)
    private UUID workOrderId;
    
    @JsonProperty(value = "part_id")
    @NotNull(message = ErrorMessages.VALIDATION_PART_ID_REQUIRED)
    private UUID partId;
    
    @JsonProperty(value = "quantity")
    @NotNull(message = ErrorMessages.VALIDATION_QUANTITY_REQUIRED)
    @Positive(message = ErrorMessages.VALIDATION_QUANTITY_POSITIVE)
    private Integer quantity;

}

