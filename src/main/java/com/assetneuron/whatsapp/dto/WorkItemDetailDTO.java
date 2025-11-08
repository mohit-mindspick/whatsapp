package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.enums.WorkItemDetailType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkItemDetailDTO {
    
    private UUID workItemId;
    private String workItemName;
    private String priority;
    private String category;
    private String assetName;
    private String locationName;
    private WorkItemDetailType type;
    private Integer taskCount;
    private String status;
}

