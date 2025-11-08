package com.assetneuron.whatsapp.model;

import com.assetneuron.whatsapp.common.persistence.BaseEntity;
import com.assetneuron.whatsapp.enums.SeverityLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cases")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cases extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "case_code", unique = true, nullable = false)
    private String caseCode;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private SeverityLevel severity;

    @Column(name = "location", length = 1000)
    private String location;

    @Column(name = "location_path", length = 1000)
    private String locationPath;

    @Column(name = "asset_id", length = 100)
    private String assetId;

    @Column(name = "asset_name", length = 500)
    private String assetName;

    @Column(name = "asset_model", length = 200)
    private String assetModel;

    @Column(name = "assigned_to", length = 200)
    private String assignedTo;

    @Column(name = "reported_date")
    private LocalDateTime reportedDate;

    @Column(name = "due_by")
    private LocalDateTime dueBy;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

}
