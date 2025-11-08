package com.assetneuron.whatsapp.model;

import com.assetneuron.whatsapp.common.persistence.BaseEntity;
import com.assetneuron.whatsapp.converter.ItemTypeConverter;
import com.assetneuron.whatsapp.enums.ItemType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wo_checklist_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChecklistItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "item_text", length = 255, nullable = false)
    private String itemText;

    @Column(name = "comment_id", columnDefinition = "UUID")
    private UUID commentId;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "is_required", nullable = false)
    @Builder.Default
    private Boolean isRequired = Boolean.TRUE;

    @Column(name = "type", length = 20, nullable = false)
    @Convert(converter = ItemTypeConverter.class)
    @Builder.Default
    private ItemType type = ItemType.FREE_TEXT;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "possible_options", columnDefinition = "jsonb")
    private String possibleOptions;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

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
