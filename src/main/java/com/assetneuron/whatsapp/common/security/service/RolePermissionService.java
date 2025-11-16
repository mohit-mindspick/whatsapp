package com.assetneuron.whatsapp.common.security.service;

import com.assetneuron.whatsapp.common.security.model.AuthPermission;
import com.assetneuron.whatsapp.common.security.model.AuthRole;
import com.assetneuron.whatsapp.common.security.repository.AuthPermissionRepository;
import com.assetneuron.whatsapp.common.security.repository.AuthRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for fetching roles and permissions for authorization.
 * Local copy from Identity service - simplified for authorization purposes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RolePermissionService {

    private final AuthRoleRepository roleRepository;
    private final AuthPermissionRepository permissionRepository;

    /**
     * Fetch roles by role codes (names).
     * 
     * @param roleCodes List of role codes to fetch
     * @return List of Role objects with their associated permissions
     */
    public List<AuthRole> getRolesByCodes(List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            List<AuthRole> roles = roleRepository.findByCodeInAndActiveTrue(roleCodes);
            
            if (roles == null || roles.isEmpty()) {
                log.warn("No active roles found for role codes: {}", roleCodes);
                return Collections.emptyList();
            }

            log.debug("Fetched {} roles from database for role codes: {}", roles.size(), roleCodes);
            return roles;
        } catch (Exception e) {
            log.error("Error fetching roles from database", e);
            return Collections.emptyList();
        }
    }

    /**
     * Fetch roles by role codes and tenant ID.
     * 
     * @param roleCodes List of role codes to fetch
     * @param tenantId Tenant ID
     * @return List of Role objects with their associated permissions
     */
    public List<AuthRole> getRolesByCodesAndTenant(List<String> roleCodes, UUID tenantId) {
        try {
            List<AuthRole> roles = roleRepository.findByCodeInAndTenantIdAndActiveTrue(roleCodes, tenantId);
            
            if (roles == null || roles.isEmpty()) {
                log.warn("No active roles found for role codes: {} and tenant: {}", roleCodes, tenantId);
                return Collections.emptyList();
            }

            log.debug("Fetched {} roles from database for role codes: {} and tenant: {}", 
                    roles.size(), roleCodes, tenantId);
            return roles;
        } catch (Exception e) {
            log.error("Error fetching roles from database for tenant: {}", tenantId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Get all permissions available in the system.
     * 
     * @return Set of all permission codes
     */
    public Set<String> getAllPermissions() {
        try {
            List<AuthPermission> permissions = permissionRepository.findAll();
            Set<String> permissionCodes = permissions.stream()
                    .map(AuthPermission::getCode)
                    .collect(Collectors.toSet());
            
            log.debug("Fetched {} permissions from database", permissionCodes.size());
            return permissionCodes;
        } catch (Exception e) {
            log.error("Error fetching all permissions from database", e);
            return Collections.emptySet();
        }
    }

    /**
     * Fetch all permissions for a given list of role codes.
     * This method handles tenant association checking and returns permissions.
     * If tenantId is provided, it will filter roles by tenant. Otherwise, it fetches without tenant filter.
     * 
     * @param roleCodes List of role codes from JWT token
     * @param tenantId Tenant ID as string (can be null or empty)
     * @return Set of unique permission codes
     */
    public Set<String> getPermissionsForRoles(List<String> roleCodes, String tenantId) {
            Set<String> permissions;
            
            try {
                UUID tenantUuid = UUID.fromString(tenantId);
                permissions = getPermissionCodesByRoleCodesAndTenant(roleCodes, tenantUuid);
                
                log.debug("Fetched {} permissions from database for roles: {} and tenant: {}", 
                        permissions.size(), roleCodes, tenantId);
                return permissions;
            } catch (Exception e) {
                log.warn("Error fetching permissions for roles: {} and tenant: {}", roleCodes, tenantId, e);
                return Collections.emptySet();
            }
    }

    /**
     * Fetch all permissions for a given list of role codes and tenant ID.
     * 
     * @param roleCodes List of role codes
     * @param tenantId Tenant ID
     * @return Set of unique permission codes
     */
    private Set<String> getPermissionCodesByRoleCodesAndTenant(List<String> roleCodes, UUID tenantId) {
        if (roleCodes == null || roleCodes.isEmpty() || tenantId == null) {
            return Collections.emptySet();
        }

        List<AuthRole> roles = getRolesByCodesAndTenant(roleCodes, tenantId);
        
        return roles.stream()
                .filter(role -> role.getDirectPermissions() != null)
                .flatMap(role -> role.getDirectPermissions().stream())
                .map(AuthPermission::getCode)
                .collect(Collectors.toSet());
    }

    /**
     * Fetch a single role by code.
     * 
     * @param roleCode Code of the role to fetch
     * @return Role object with its associated permissions, or null if not found
     */
    public AuthRole getRoleByCode(String roleCode) {
        if (roleCode == null || roleCode.isEmpty()) {
            return null;
        }

        try {
            Optional<AuthRole> roleOpt = roleRepository.findByCode(roleCode);
            
            if (roleOpt.isPresent()) {
                AuthRole role = roleOpt.get();
                if (role.getActive() != null && role.getActive()) {
                    log.debug("Fetched role '{}' from database with {} permissions", 
                            roleCode, 
                            role.getDirectPermissions() != null ? role.getDirectPermissions().size() : 0);
                    return role;
                } else {
                    log.warn("Role '{}' is not active", roleCode);
                    return null;
                }
            }

            return null;
        } catch (Exception e) {
            log.error("Error fetching role '{}' from database", roleCode, e);
            return null;
        }
    }
}

