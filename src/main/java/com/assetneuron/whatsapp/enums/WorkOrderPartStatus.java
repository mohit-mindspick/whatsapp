package com.assetneuron.whatsapp.enums;

import lombok.Getter;

@Getter
public enum WorkOrderPartStatus {
    AVAILABLE ("Available", "Part is available"),
    UNAVAILABLE ("Unavailable", "Part is unavailable"),
    COLLECTED("Collected", "Part has been collected"),
    RETURNED("Returned", "Part has been returned");

    private final String label;
    private final String description;

    WorkOrderPartStatus(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public static WorkOrderPartStatus fromLabel(String label) {
        for (WorkOrderPartStatus status : values()) {
            if (status.getLabel().equalsIgnoreCase(label)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid WorkOrderPartStatus label: " + label);
    }

    public static WorkOrderPartStatus fromCode(String code) {
        for (WorkOrderPartStatus status : values()) {
            if (status.name().equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid WorkOrderPartStatus code: " + code);
    }
}
