package com.assetneuron.whatsapp.enums;

import lombok.Getter;

@Getter
public enum WorkOrderType {

    PREVENTIVE ("PREVENTIVE", "Preventive"),
    CORRECTIVE ("CORRECTIVE", "Corrective"),
    INSPECTION ("INSPECTION", "Inspection");

    private final String code;
    private final String label;

    WorkOrderType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static WorkOrderType fromCode(String code) {
        for (WorkOrderType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }

}
