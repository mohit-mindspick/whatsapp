package com.assetneuron.whatsapp.common.adaptor.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for inventory API status update
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStatusUpdateRequest {
    private String partCode;
    private String status;
}

