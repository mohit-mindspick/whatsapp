package com.assetneuron.whatsapp.enums;

/**
 * Enum representing severity levels with display names and colors for UI
 */
public enum SeverityLevel {
    LOW("Low", "#4CAF50"),           // Green
    MEDIUM("Medium", "#FF9800"),     // Orange
    HIGH("High", "#FF5722"),         // Deep Orange
    CRITICAL("Critical", "#F44336"); // Red

    private final String displayName;
    private final String color;

    SeverityLevel(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }
}
