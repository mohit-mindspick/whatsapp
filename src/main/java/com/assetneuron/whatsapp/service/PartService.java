package com.assetneuron.whatsapp.service;

import com.assetneuron.whatsapp.common.adaptor.HttpClientAdapter;
import com.assetneuron.whatsapp.common.adaptor.HttpClientResponse;
import com.assetneuron.whatsapp.config.ReadOnly;
import com.assetneuron.whatsapp.dto.CollectPartRequest;
import com.assetneuron.whatsapp.dto.PartDTO;
import com.assetneuron.whatsapp.dto.ReturnPartRequest;
import com.assetneuron.whatsapp.dto.WorkOrderPartDTO;
import com.assetneuron.whatsapp.dto.WorkOrderServiceUpdateParts;
import com.assetneuron.whatsapp.dto.WorkOrderServiceUpdatePartsRequest;
import com.assetneuron.whatsapp.model.WorkOrderPart;
import com.assetneuron.whatsapp.repository.WorkOrderPartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartService {

    private final WorkOrderPartRepository workOrderPartRepository;

    private final HttpClientAdapter httpClientAdapter;

    @Value("${workorder.service.url:http://localhost:8080}")
    private String workOrderServiceUrl;

    @ReadOnly
    public List<PartDTO> getWorkOrderParts(UUID workOrderId, UUID tenantId) {
        log.info("Getting parts for work order id: {}, tenant id: {}", workOrderId, tenantId);

        List<WorkOrderPart> parts = workOrderPartRepository.findByWorkOrderIdAndTenantId(workOrderId, tenantId);

        return parts.stream()
                .map(this::mapWorkOrderPartToDto)
                .collect(Collectors.toList());
    }

    public HttpClientResponse<Object> updateWorkOrderParts(UUID workOrderId, List<WorkOrderPartDTO> parts, UUID tenantId) {
        int partsCount = parts != null ? parts.size() : 0;
        log.info("Updating parts for work order id: {}, tenant id: {}, parts count: {}",
                workOrderId, tenantId, partsCount);

        // Convert WorkOrderPartDTO list to WorkOrderServiceUpdateParts list using stream
        List<WorkOrderServiceUpdateParts> partList = parts != null && !parts.isEmpty()
                ? parts.stream()
                .map(part -> WorkOrderServiceUpdateParts.builder()
                        .id(part.getId())
                        .partId(part.getPartId())
                        .quantity(part.getQuantity())
                        .status(part.getStatus())
                        .build())
                .collect(Collectors.toList())
                : new ArrayList<>();

        // Build request payload for work order service
        WorkOrderServiceUpdatePartsRequest workOrderRequest = WorkOrderServiceUpdatePartsRequest.builder()
                .workOrderId(workOrderId)
                .parts(partList)
                .build();

        // Construct URL for work order service
        String url = workOrderServiceUrl + "/api/v1/workorders/" + workOrderId + "/parts/all";

        // Make HTTP PUT call to work order service (X-Tenant-Id is automatically included by HttpClientAdapter)
        HttpClientResponse<Object> response = httpClientAdapter.put(url, workOrderRequest, Object.class);

        log.info("Parts updated successfully - Work Order ID: {}, Parts Count: {}, Response Status: {}",
                workOrderId, partsCount, response.getStatusCode());

        return response;
    }

    public HttpClientResponse<Object> returnPart(ReturnPartRequest request, UUID tenantId) {
        log.info("Returning part for work order id: {}, part id: {}, quantity: {}, tenant id: {}",
                request.getWorkOrderId(), request.getPartId(), request.getQuantity(), tenantId);

        // Build query parameters for work order service
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("workOrderId", request.getWorkOrderId().toString());
        queryParams.put("partId", request.getPartId().toString());
        queryParams.put("quantity", request.getQuantity().toString());

        // Construct URL for work order service
        String url = workOrderServiceUrl + "/api/v1/workorders/" + request.getWorkOrderId() + "/parts/return";

        // Make HTTP PUT call to work order service with query parameters (X-Tenant-Id is automatically included by HttpClientAdapter)
        HttpClientResponse<Object> response = httpClientAdapter.put(url, queryParams, Object.class);

        log.info("Part returned successfully - Work Order ID: {}, Part ID: {}, Quantity: {}, Response Status: {}",
                request.getWorkOrderId(), request.getPartId(), request.getQuantity(), response.getStatusCode());

        return response;
    }

    public HttpClientResponse<Object> collectPart(CollectPartRequest request, UUID tenantId) {
        log.info("Collecting part for work order id: {}, part id: {}, quantity: {}, tenant id: {}",
                request.getWorkOrderId(), request.getPartId(), request.getQuantity(), tenantId);

        // Build query parameters for work order service
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("workOrderId", request.getWorkOrderId().toString());
        queryParams.put("partId", request.getPartId().toString());
        queryParams.put("quantity", request.getQuantity().toString());

        // Construct URL for work order service
        String url = workOrderServiceUrl + "/api/v1/workorders/" + request.getWorkOrderId() + "/parts/collect";

        // Make HTTP PUT call to work order service with query parameters (X-Tenant-Id is automatically included by HttpClientAdapter)
        HttpClientResponse<Object> response = httpClientAdapter.put(url, queryParams, Object.class);

        log.info("Part collected successfully - Work Order ID: {}, Part ID: {}, Quantity: {}, Response Status: {}",
                request.getWorkOrderId(), request.getPartId(), request.getQuantity(), response.getStatusCode());

        return response;
    }

    private PartDTO mapWorkOrderPartToDto(WorkOrderPart workOrderPart) {
        return PartDTO.builder()
                .workOrderPartId(workOrderPart.getId())
                .partId(workOrderPart.getPartId())
                .partCode(workOrderPart.getPartCode())
                .quantity(workOrderPart.getQuantity())
                .status(workOrderPart.getStatus())
                .build();
    }

}

