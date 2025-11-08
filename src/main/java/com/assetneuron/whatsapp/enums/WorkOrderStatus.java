package com.assetneuron.whatsapp.enums;

import lombok.Getter;

@Getter
public enum WorkOrderStatus {
    
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    ON_HOLD("On Hold"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    CLOSED("Closed");

    private final String label;

    WorkOrderStatus(String label) {
        this.label = label;
    }

    public String getCode() {
        return this.name();
    }

    public static WorkOrderStatus fromCode(String code) {
        for (WorkOrderStatus status : values()) {
            if (status.name().equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid work order status: " + code);
    }

    public static WorkOrderStatus fromLabel(String label) {
        for (WorkOrderStatus status : values()) {
            if (status.getLabel().equalsIgnoreCase(label)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid work order status label: " + label);
    }
}
