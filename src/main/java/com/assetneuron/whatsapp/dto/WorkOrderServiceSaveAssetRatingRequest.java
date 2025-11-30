package com.assetneuron.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for saving asset rating in the work order service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderServiceSaveAssetRatingRequest {

    @NotNull(message = "Work order ID is required")
    @JsonProperty(value = "work_order_id")
    private UUID workOrderId;

    @NotNull(message = "User ID is required")
    @JsonProperty(value = "user_id")
    private UUID userId;

    @NotNull(message = "Asset ID is required")
    @JsonProperty(value = "asset_id")
    private UUID assetId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

}

