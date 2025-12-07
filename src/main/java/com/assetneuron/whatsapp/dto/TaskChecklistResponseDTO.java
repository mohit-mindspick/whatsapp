package com.assetneuron.whatsapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskChecklistResponseDTO {
    
    private UUID id;
    private Integer sequence;
    private Integer countOfChecklist;
    private List<ChecklistItemDTO> checklistItems;
}

