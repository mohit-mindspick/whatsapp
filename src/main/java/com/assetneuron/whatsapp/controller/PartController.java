package com.assetneuron.whatsapp.controller;

import com.assetneuron.whatsapp.common.model.ApiResponse;
import com.assetneuron.whatsapp.dto.PartDTO;
import com.assetneuron.whatsapp.dto.ReturnPartRequest;
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
import org.springframework.web.bind.annotation.RequestHeader;
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

    @GetMapping("/workorder/{workOrderId}")
    public ResponseEntity<ApiResponse<List<PartDTO>>> getWorkOrderParts(
            @PathVariable UUID workOrderId,
            @RequestHeader(value = "X-Tenant-Id", required = true) UUID tenantId,
            Authentication authentication) {

        try {
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
    public ResponseEntity<ApiResponse<Void>> updateWorkOrderParts(
            @PathVariable UUID workOrderId,
            @Valid @RequestBody List<PartDTO> parts,
            @RequestHeader(value = "X-Tenant-Id", required = true) UUID tenantId,
            Authentication authentication) {

        try {
            partService.updateWorkOrderParts(workOrderId, parts, tenantId);

            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Parts updated successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error updating parts for work order id: {}", workOrderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to update parts: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/return")
    public ResponseEntity<ApiResponse<Void>> returnPart(
            @Valid @RequestBody ReturnPartRequest request,
            @RequestHeader(value = "X-Tenant-Id", required = true) UUID tenantId,
            Authentication authentication) {

        try {
            partService.returnPart(request, tenantId);

            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Part returned successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error returning part for work order id: {}, part id: {}",
                    request.getWorkOrderId(), request.getPartId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to return part: " + e.getMessage())
                            .build());
        }
    }

}

