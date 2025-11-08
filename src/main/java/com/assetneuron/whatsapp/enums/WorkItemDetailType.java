package com.assetneuron.whatsapp.enums;

import lombok.Getter;

@Getter
public enum WorkItemDetailType {
    DEFAULT("DEFAULT", "Default"),
    PREVENTATIVE("PREVENTATIVE", "Preventative");

    private final String code;
    private final String label;

    WorkItemDetailType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static WorkItemDetailType fromCode(String code) {
        for (WorkItemDetailType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}

