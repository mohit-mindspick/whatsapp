package com.assetneuron.whatsapp.service;

import com.assetneuron.whatsapp.config.ReadOnly;
import com.assetneuron.whatsapp.config.WriteOnly;
import com.assetneuron.whatsapp.dto.LogHoursRequest;
import com.assetneuron.whatsapp.dto.MyWorkDTO;
import com.assetneuron.whatsapp.dto.TaskDTO;
import com.assetneuron.whatsapp.dto.ViewMyWorkRequest;
import com.assetneuron.whatsapp.dto.ViewWorkOrderDetailRequest;
import com.assetneuron.whatsapp.dto.WorkItemDetailDTO;
import com.assetneuron.whatsapp.dto.WorkItemTasksResponseDTO;
import com.assetneuron.whatsapp.enums.WorkItemDetailType;
import com.assetneuron.whatsapp.repository.WorkItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkItemService {

    private final WorkItemRepository workItemRepository;

    @ReadOnly
    public List<MyWorkDTO> viewMyWork(ViewMyWorkRequest request) {
        log.info("Getting work orders and cases for phone number: {} between {} and {}", 
                request.getPhoneNumber(), request.getStartDate(), request.getEndDate());
        
        List<Object[]> results = workItemRepository.findMyWorkByUserPhoneNumberAndTimeRange(
                request.getPhoneNumber(),
                request.getStartDate(),
                request.getEndDate());
        
        List<MyWorkDTO> myWorkOrders = new ArrayList<>();

        for (Object[] result : results) {
            UUID id = (UUID) result[0];
            String title = (String) result[1];
            String code = (String) result[2];
            String importance = (String) result[3];
            String type = (String) result[4];

            myWorkOrders.add(MyWorkDTO.builder()
                    .id(id)
                    .name(title)
                    .code(code)
                    .priority(importance)
                    .type(type)
                    .build());
        }
        
        log.info("Found {} work items for phone number: {}",
                myWorkOrders.size(), request.getPhoneNumber());
        
        return myWorkOrders;
    }

    @ReadOnly
    public WorkItemDetailDTO getWorkItemDetail(ViewWorkOrderDetailRequest request) {
        log.info("Getting work item detail for phone number: {}, work item id: {}, item type: {}",
                request.getPhoneNumber(), request.getWorkItemId(), request.getItemType());
        
        // TODO: Replace with actual database query logic
        // For now, returning dummy data
        return WorkItemDetailDTO.builder()
                .workItemId(request.getWorkItemId())
                .workItemName("Sample Work Order")
                .priority("HIGH")
                .category("Maintenance")
                .assetName("Asset-001")
                .locationName("Building A - Floor 2")
                .type(WorkItemDetailType.PREVENTATIVE)
                .taskCount(5)
                .status("IN_PROGRESS")
                .build();
    }

    @ReadOnly
    public WorkItemTasksResponseDTO getWorkItemTasks(UUID workItemId, UUID tenantId) {
        log.info("Getting tasks for work item id: {}, tenant id:", workItemId, tenantId);
        
        // TODO: Replace with actual database query logic
        // For now, returning dummy data
        List<TaskDTO> tasks = new ArrayList<>();
        tasks.add(TaskDTO.builder()
                .taskId(UUID.randomUUID())
                .sequence(1)
                .name("Task 1")
                .instruction("Follow safety procedures before starting")
                .duration("30 minutes")
                .build());
        tasks.add(TaskDTO.builder()
                .taskId(UUID.randomUUID())
                .sequence(2)
                .name("Task 2")
                .instruction("Inspect equipment thoroughly")
                .duration("45 minutes")
                .build());
        tasks.add(TaskDTO.builder()
                .taskId(UUID.randomUUID())
                .sequence(3)
                .name("Task 3")
                .instruction("Complete maintenance checklist")
                .duration("60 minutes")
                .build());
        
        return WorkItemTasksResponseDTO.builder()
                .tasks(tasks)
                .totalTask(tasks.size())
                .build();
    }

    @WriteOnly
    public void logHours(LogHoursRequest request, UUID tenantId) {
        log.info("Logging hours for work item id: {}, user id: {}, time in hours: {} hours, tenant id: {}", 
                request.getWorkItemId(), request.getUserId(), request.getTimeInHours(), tenantId);
        
        // TODO: Replace with actual database logic to log hours
        // For now, just logging the operation
        log.debug("Logging hours - Work Item ID: {}, User ID: {}, Time In Hours: {} hours", 
                request.getWorkItemId(), request.getUserId(), request.getTimeInHours());
    }
}

