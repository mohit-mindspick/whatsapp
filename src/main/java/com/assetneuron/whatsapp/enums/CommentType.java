package com.assetneuron.whatsapp.enums;

import lombok.Getter;

@Getter
public enum CommentType {
    TEXT("TEXT", "Text"),
    IMAGE("IMAGE", "Image"),
    MEDIA("MEDIA", "Media"),
    VOICE("VOICE", "Voice");

    private final String code;
    private final String label;

    CommentType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static CommentType fromCode(String code) {
        for (CommentType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown comment type code: " + code);
    }
}

