package com.assetneuron.whatsapp.model;

import com.assetneuron.whatsapp.common.persistence.BaseEntity;
import com.assetneuron.whatsapp.converter.WorkOrderStatusConverter;
import com.assetneuron.whatsapp.converter.WorkOrderTypeConverter;
import com.assetneuron.whatsapp.enums.WorkOrderStatus;
import com.assetneuron.whatsapp.enums.WorkOrderType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "wo_work_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WorkOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    @Column(name = "type", length = 15, nullable = false)
    @Convert(converter = WorkOrderTypeConverter.class)
    private WorkOrderType type;

    @Column(name = "priority", nullable = false)
    private String priorityCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "priority", referencedColumnName = "code", insertable = false, updatable = false),
            @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id", insertable = false, updatable = false)
    })
    private Priority priority;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "asset_id", columnDefinition = "UUID")
    private UUID assetId;

    @Column(name = "asset_name")
    private String assetName;

    @Column(name = "location_id", columnDefinition = "UUID")
    private String locationId;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;

    @Column(name = "assigned_to", columnDefinition = "UUID")
    private UUID assignedTo;

    @Column(name = "assigned_to_name")
    private String assignedToName;

    @Column(name = "status", length = 20, nullable = false)
    @Convert(converter = WorkOrderStatusConverter.class)
    @Builder.Default
    private WorkOrderStatus status = WorkOrderStatus.OPEN;

    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks;

    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkOrderPart> workOrderParts;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
