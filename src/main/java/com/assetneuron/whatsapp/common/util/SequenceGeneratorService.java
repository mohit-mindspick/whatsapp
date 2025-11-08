package com.assetneuron.whatsapp.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for generating sequential codes for entities per tenant.
 * Uses database sequences to ensure uniqueness and consistency.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SequenceGeneratorService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Generates a formatted code for a given entity type and tenant.
     * Checks if the generated code already exists and generates the next available code if it does.
     *
     * @param tenantId The tenant ID
     * @param entityType The entity type (e.g., "site", "building", "floor", "room", "open_area")
     * @return A formatted code string (e.g., "SITE-00001", "BLD-00001")
     */
    public String generateCode(UUID tenantId, String entityType) {
        log.debug("Generating code for entityType: {} and tenantId: {}", entityType, tenantId);

        String prefix = this.getEntityPrefix(entityType);
        int maxAttempts = 100; // Prevent infinite loops
        int attempts = 0;

        while (attempts < maxAttempts) {
            attempts++;

            // Get next sequence number from database
            Long nextSeq = jdbcTemplate.queryForObject(
                    "SELECT reserve_tenant_sequence(?, ?)",
                    Long.class,
                    tenantId,
                    entityType
            );

            // Format the code based on entity type
            String formattedCode = String.format("%s-%05d", prefix, nextSeq);

            // Check if this code already exists in the work_orders table
            boolean codeExists = checkCodeExists(tenantId, formattedCode);

            if (!codeExists) {
                log.debug("Generated unique code: {} for entityType: {} and tenantId: {} (attempt {})",
                        formattedCode, entityType, tenantId, attempts);
                return formattedCode;
            } else {
                log.debug("Code {} already exists for tenantId: {}, generating next sequence (attempt {})",
                        formattedCode, tenantId, attempts);
            }
        }

        // If we've exhausted all attempts, throw an exception
        throw new RuntimeException("Unable to generate unique code for entityType: " + entityType +
                " and tenantId: " + tenantId + " after " + maxAttempts + " attempts");
    }

    /**
     * Checks if a code already exists in the work_order table for the given tenant.
     *
     * @param tenantId The tenant ID
     * @param code The code to check
     * @return true if the code exists, false otherwise
     */
    private boolean checkCodeExists(UUID tenantId, String code) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM wo_work_order WHERE code = ? AND tenant_id = ?",
                Integer.class,
                code,
                tenantId
        );
        return count != null && count > 0;
    }

    /**
     * Gets the prefix for a given entity type.
     *
     * @param entityType The entity type
     * @return The prefix for the code
     */
    private String getEntityPrefix(String entityType) {
        switch (entityType.toLowerCase()) {
            case "work_order":
                return "WO";
            default:
                log.warn("Unknown entity type: {}, using default prefix", entityType);
                return "WO";
        }
    }
}
