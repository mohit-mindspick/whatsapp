package com.assetneuron.whatsapp.common.security.repository;

import com.assetneuron.whatsapp.common.security.model.AuthPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Permission entity.
 * Local copy from Identity service for authorization purposes.
 */
@Repository
public interface AuthPermissionRepository extends JpaRepository<AuthPermission, UUID> {
    
    Optional<AuthPermission> findById(UUID id);
    
    Optional<AuthPermission> findByCode(String code);
    
    boolean existsByCode(String code);
    
    List<AuthPermission> findAll();
    
    List<AuthPermission> findByNameContainingIgnoreCase(String name);
    
    List<AuthPermission> findByDescriptionContainingIgnoreCase(String description);
}


