package com.assetneuron.whatsapp.service;

import com.assetneuron.whatsapp.config.ReadOnly;
import com.assetneuron.whatsapp.dto.ChecklistItemDTO;
import com.assetneuron.whatsapp.dto.TaskChecklistResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    @ReadOnly
    public TaskChecklistResponseDTO getTaskChecklistItems(UUID workOrderId, UUID taskId, UUID tenantId) {
        log.info("Getting checklist items for work order id: {}, task id: {}, tenant id: {}", 
                workOrderId, taskId, tenantId);
        
        // TODO: Replace with actual database query logic
        // For now, returning dummy data
        List<ChecklistItemDTO> checklistItems = new ArrayList<>();
        checklistItems.add(ChecklistItemDTO.builder()
                .sequence(1)
                .id(UUID.randomUUID())
                .description("Check safety equipment before starting")
                .instruction("Ensure all safety equipment is properly worn and functional")
                .inputType("YES_NO")
                .build());
        checklistItems.add(ChecklistItemDTO.builder()
                .sequence(2)
                .id(UUID.randomUUID())
                .description("Verify all tools are available")
                .instruction("Check that all required tools are present and in good condition")
                .inputType("YES_NO")
                .build());
        checklistItems.add(ChecklistItemDTO.builder()
                .sequence(3)
                .id(UUID.randomUUID())
                .description("Inspect equipment for any visible damage")
                .instruction("Look for cracks, wear, or other signs of damage")
                .inputType("PASS_FAIL")
                .build());
        checklistItems.add(ChecklistItemDTO.builder()
                .sequence(4)
                .id(UUID.randomUUID())
                .description("Confirm work area is clean and safe")
                .instruction("Ensure the work area is free of hazards and properly organized")
                .inputType("FREE_TEXT")
                .build());
        
        return TaskChecklistResponseDTO.builder()
                .taskSequence(1)
                .countOfChecklistItem(checklistItems.size())
                .checklistItems(checklistItems)
                .build();
    }
}

