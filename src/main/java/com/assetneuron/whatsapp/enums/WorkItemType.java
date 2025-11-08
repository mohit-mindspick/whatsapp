package com.assetneuron.whatsapp.enums;

import lombok.Getter;

@Getter
public enum WorkItemType {
    WORKORDER("WORKORDER", "Work Order"),
    CASE("CASE", "Case");

    private final String code;
    private final String label;

    WorkItemType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static WorkItemType fromCode(String code) {
        for (WorkItemType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}

