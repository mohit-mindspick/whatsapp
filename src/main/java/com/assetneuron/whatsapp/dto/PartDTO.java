package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.enums.WorkOrderPartStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartDTO {
    
    private UUID workOrderPartId;
    private UUID partId;
    private String partCode;
    private Integer quantity;
    private WorkOrderPartStatus status;
}

