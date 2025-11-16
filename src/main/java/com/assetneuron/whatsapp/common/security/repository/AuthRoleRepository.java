package com.assetneuron.whatsapp.common.security.repository;

import com.assetneuron.whatsapp.common.model.RoleType;
import com.assetneuron.whatsapp.common.security.model.AuthRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Role entity.
 * Local copy from Identity service for authorization purposes.
 */
@Repository
public interface AuthRoleRepository extends JpaRepository<AuthRole, UUID> {
    
    Optional<AuthRole> findByCode(String code);
    
    List<AuthRole> findByRoleType(RoleType roleType);
    
    boolean existsByCode(String code);
    
    List<AuthRole> findByNameContainingIgnoreCase(String name);
    
    // Tenant-specific queries
    Optional<AuthRole> findByCodeAndTenantId(String code, UUID tenantId);
    
    List<AuthRole> findByTenantId(UUID tenantId);
    
    List<AuthRole> findByTenantIdAndRoleType(UUID tenantId, RoleType roleType);
    
    List<AuthRole> findByTenantIdAndNameContainingIgnoreCase(UUID tenantId, String name);
    
    boolean existsByCodeAndTenantId(String code, UUID tenantId);
    
    // Find roles by codes (for authorization)
    List<AuthRole> findByCodeIn(List<String> codes);
    
    // Find roles by codes and tenant
    List<AuthRole> findByCodeInAndTenantId(List<String> codes, UUID tenantId);
    
    // Find active roles by codes
    List<AuthRole> findByCodeInAndActiveTrue(List<String> codes);
    
    // Find active roles by codes and tenant
    List<AuthRole> findByCodeInAndTenantIdAndActiveTrue(List<String> codes, UUID tenantId);
}

