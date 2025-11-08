package com.assetneuron.whatsapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskChecklistResponseDTO {
    
    private Integer taskSequence;
    private Integer countOfChecklistItem;
    private List<ChecklistItemDTO> checklistItems;
}

