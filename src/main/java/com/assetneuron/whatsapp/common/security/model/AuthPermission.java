package com.assetneuron.whatsapp.common.security.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Permission model for authorization.
 * Local copy from Identity service.
 */
@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthPermission {
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "code", unique = true, nullable = false)
    private String code; // e.g., "USER_CREATE"

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;
}


