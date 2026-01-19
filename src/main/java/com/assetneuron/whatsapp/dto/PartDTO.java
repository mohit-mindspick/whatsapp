package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.enums.WorkOrderPartStatus;
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
public class PartDTO {
    
    private UUID workOrderPartId;
    private UUID partId;
    private String partCode;
    private BigDecimal quantity;
    private WorkOrderPartStatus status;
}

