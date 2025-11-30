package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.common.constant.ErrorMessages;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveAssetRatingRequest {
    
    @JsonProperty(value = "work_order_id")
    @NotNull(message = ErrorMessages.VALIDATION_WORK_ORDER_ID_REQUIRED)
    private UUID workOrderId;
    
    @JsonProperty(value = "user_id")
    @NotNull(message = ErrorMessages.VALIDATION_USER_ID_REQUIRED)
    private UUID userId;
    
    @JsonProperty(value = "asset_id")
    @NotNull(message = ErrorMessages.VALIDATION_ASSET_ID_REQUIRED)
    private UUID assetId;
    
    @JsonProperty(value = "rating")
    @NotNull(message = ErrorMessages.VALIDATION_RATING_REQUIRED)
    @Min(value = 1, message = ErrorMessages.VALIDATION_RATING_MIN)
    @Max(value = 5, message = ErrorMessages.VALIDATION_RATING_MAX)
    private Integer rating;
}

