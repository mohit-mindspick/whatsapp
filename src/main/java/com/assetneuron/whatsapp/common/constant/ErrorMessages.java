package com.assetneuron.whatsapp.common.constant;

public final class ErrorMessages {

    private ErrorMessages() {
        // Private constructor to prevent instantiation
    }

    // Phone number validation
    public static final String VALIDATION_PHONE_NUMBER_REQUIRED = "Phone number is required";

    // Work Item validation
    public static final String VALIDATION_WORK_ITEM_ID_REQUIRED = "Work Item ID is required";
    public static final String VALIDATION_WORK_ITEM_ID_REQUIRED_LOWER = "Work item id is required";

    // User validation
    public static final String VALIDATION_USER_ID_REQUIRED = "User ID is required";

    // Time validation
    public static final String VALIDATION_TIME_IN_HOURS_REQUIRED = "Time in hours is required";
    public static final String VALIDATION_TIME_IN_HOURS_POSITIVE = "Time in hours must be positive";

    // Work Order validation
    public static final String VALIDATION_WORK_ORDER_ID_REQUIRED = "Work Order ID is required";

    // Part validation
    public static final String VALIDATION_PART_ID_REQUIRED = "Part ID is required";
    public static final String VALIDATION_PART_NAME_REQUIRED = "Part Name is required";
    public static final String VALIDATION_QUANTITY_REQUIRED = "Quantity is required";
    public static final String VALIDATION_QUANTITY_POSITIVE = "Quantity must be positive";

    // Date validation
    public static final String VALIDATION_START_DATE_REQUIRED = "Start date is required";
    public static final String VALIDATION_END_DATE_REQUIRED = "End date is required";

    // Item type validation
    public static final String VALIDATION_ITEM_TYPE_REQUIRED = "Item type is required";

}