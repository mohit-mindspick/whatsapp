package com.assetneuron.whatsapp.common.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenant_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
    
    @Column(name = "locale", length = 10)
    private String locale;
    
    @Column(name = "timezone", length = 50)
    private String timezone;
    
    @Column(name = "session_timeout", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(
        read = "session_timeout::text",
        write = "?::jsonb"
    )
    private String sessionTimeout;
    
    @Column(name = "additional_settings", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(
        read = "additional_settings::text",
        write = "?::jsonb"
    )
    private String additionalSettings;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
    
    @Column(name = "multi_device_enabled", nullable = false)
    @Builder.Default
    private Boolean multiDeviceEnabled = false;
}

