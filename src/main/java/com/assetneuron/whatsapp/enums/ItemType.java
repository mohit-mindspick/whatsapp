package com.assetneuron.whatsapp.enums;

import lombok.Getter;

@Getter
public enum ItemType {
    FREE_TEXT("Free Text"),
    YES_NO("Yes/No"),
    PASS_FAIL("Pass/Fail"),
    DROPDOWN("Dropdown"),
    NUMERIC("Numeric");

    private final String label;

    ItemType(String label) {
        this.label = label;
    }
}
