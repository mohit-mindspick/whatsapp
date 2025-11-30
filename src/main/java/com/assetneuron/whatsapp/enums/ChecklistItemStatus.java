package com.assetneuron.whatsapp.enums;

import lombok.Getter;

@Getter
public enum ChecklistItemStatus {
    
    NOT_STARTED("Not Started"),
    COMPLETED("Completed");

    private final String label;

    ChecklistItemStatus(String label) {
        this.label = label;
    }

    public String getCode() {
        return this.name();
    }

    public static ChecklistItemStatus fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return NOT_STARTED; // Default value
        }
        for (ChecklistItemStatus status : values()) {
            if (status.name().equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid checklist item status: " + code);
    }

    public static ChecklistItemStatus fromLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            return NOT_STARTED; // Default value
        }
        for (ChecklistItemStatus status : values()) {
            if (status.getLabel().equalsIgnoreCase(label)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid checklist item status label: " + label);
    }
}

