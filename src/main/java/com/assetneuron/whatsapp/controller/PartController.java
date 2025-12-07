package com.assetneuron.whatsapp.controller;

import com.assetneuron.whatsapp.common.adaptor.HttpClientResponse;
import com.assetneuron.whatsapp.common.adaptor.RequestTokenUtil;
import com.assetneuron.whatsapp.common.model.ApiResponse;
import com.assetneuron.whatsapp.dto.CollectPartRequest;
import com.assetneuron.whatsapp.dto.PartDTO;
import com.assetneuron.whatsapp.dto.ReturnPartRequest;
import com.assetneuron.whatsapp.dto.WorkOrderPartDTO;
import com.assetneuron.whatsapp.service.PartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/part")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Part APIs", description = "APIs for managing work order parts")
public class PartController {

    private final PartService partService;
    private final RequestTokenUtil requestTokenUtil;

    @GetMapping("/workorder/{workOrderId}")
    public ResponseEntity<ApiResponse<List<PartDTO>>> getWorkOrderParts(
            @PathVariable UUID workOrderId,
            Authentication authentication) {

        try {
            // Extract tenant ID from JWT token (validates token and tenant ID presence)
            UUID tenantId = requestTokenUtil.getTenantIdFromToken();
            List<PartDTO> parts = partService.getWorkOrderParts(workOrderId, tenantId);

            return ResponseEntity.ok(ApiResponse.<List<PartDTO>>builder()
                    .success(true)
                    .data(parts)
                    .message("Parts retrieved successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving parts for work order id: {}", workOrderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<PartDTO>>builder()
                            .success(false)
                            .message("Failed to retrieve parts: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/workorder/{workOrderId}")
    public ResponseEntity<ApiResponse<Object>> updateWorkOrderParts(
            @PathVariable UUID workOrderId,
            @Valid @RequestBody List<WorkOrderPartDTO> parts,
            Authentication authentication) {

        try {
            // Extract tenant ID from JWT token (validates token and tenant ID presence)
            UUID tenantId = requestTokenUtil.getTenantIdFromToken();
            HttpClientResponse<Object> response = partService.updateWorkOrderParts(workOrderId, parts, tenantId);

            // Return the response from work order service
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.<Object>builder()
                                .success(true)
                                .data(response.getBody())
                                .message("Parts updated successfully")
                                .build());
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.<Object>builder()
                                .success(false)
                                .data(response.getBody())
                                .message("Failed to update parts")
                                .build());
            }
        } catch (Exception e) {
            log.error("Error updating parts for work order id: {}", workOrderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Object>builder()
                            .success(false)
                            .message("Failed to update parts: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/return")
    public ResponseEntity<ApiResponse<Object>> returnPart(
            @Valid @RequestBody ReturnPartRequest request,
            Authentication authentication) {

        try {
            // Extract tenant ID from JWT token (validates token and tenant ID presence)
            UUID tenantId = requestTokenUtil.getTenantIdFromToken();
            HttpClientResponse<Object> response = partService.returnPart(request, tenantId);

            // Return the response from work order service
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.<Object>builder()
                                .success(true)
                                .data(response.getBody())
                                .message("Part returned successfully")
                                .build());
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.<Object>builder()
                                .success(false)
                                .data(response.getBody())
                                .message("Failed to return part")
                                .build());
            }
        } catch (Exception e) {
            log.error("Error returning part for work order id: {}, part id: {}",
                    request.getWorkOrderId(), request.getPartId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Object>builder()
                            .success(false)
                            .message("Failed to return part: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/collect")
    public ResponseEntity<ApiResponse<Object>> collectPart(
            @Valid @RequestBody CollectPartRequest request,
            Authentication authentication) {

        try {
            // Extract tenant ID from JWT token (validates token and tenant ID presence)
            UUID tenantId = requestTokenUtil.getTenantIdFromToken();
            HttpClientResponse<Object> response = partService.collectPart(request, tenantId);

            // Return the response from work order service
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.<Object>builder()
                                .success(true)
                                .data(response.getBody())
                                .message("Part collected successfully")
                                .build());
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.<Object>builder()
                                .success(false)
                                .data(response.getBody())
                                .message("Failed to collect part")
                                .build());
            }
        } catch (Exception e) {
            log.error("Error collecting part for work order id: {}, part id: {}",
                    request.getWorkOrderId(), request.getPartId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Object>builder()
                            .success(false)
                            .message("Failed to collect part: " + e.getMessage())
                            .build());
        }
    }

}

