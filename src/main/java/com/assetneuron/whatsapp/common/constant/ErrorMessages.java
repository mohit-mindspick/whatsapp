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

    // Work Order Part validation
    public static final String VALIDATION_WORK_ORDER_PART_ID_REQUIRED = "Work Order Part ID is required";

    // Part validation
    public static final String VALIDATION_PART_ID_REQUIRED = "Part ID is required";
    public static final String VALIDATION_PART_NAME_REQUIRED = "Part Name is required";
    public static final String VALIDATION_PART_STATUS_REQUIRED = "Part Status is required";
    public static final String VALIDATION_PART_STATUS_MUST_BE_COLLECTED = "Part status must be COLLECTED";
    public static final String VALIDATION_QUANTITY_REQUIRED = "Quantity is required";
    public static final String VALIDATION_QUANTITY_POSITIVE = "Quantity must be positive";

    // Date validation
    public static final String VALIDATION_DUE_DATE_REQUIRED = "Due date is required";
    public static final String VALIDATION_DATE_FILTER_REQUIRED = "Date filter is required";

    // Item type validation
    public static final String VALIDATION_ITEM_TYPE_REQUIRED = "Item type is required";

    // Asset validation
    public static final String VALIDATION_ASSET_ID_REQUIRED = "Asset ID is required";

    // Rating validation
    public static final String VALIDATION_RATING_REQUIRED = "Rating is required";
    public static final String VALIDATION_RATING_MIN = "Rating must be at least 1";
    public static final String VALIDATION_RATING_MAX = "Rating must be at most 5";

    // Task validation
    public static final String VALIDATION_TASK_ID_REQUIRED = "Task ID is required";

    // Checklist validation
    public static final String VALIDATION_CHECKLIST_ITEM_ID_REQUIRED = "Checklist Item ID is required";
    public static final String VALIDATION_ITEM_TEXT_REQUIRED = "Item Text is required";
    public static final String VALIDATION_CHECKLIST_ITEMS_REQUIRED = "Checklist items are required";

    // Comment validation
    public static final String VALIDATION_COMMENT_REQUIRED = "Comment is required";
    public static final String VALIDATION_COMMENT_ID_REQUIRED = "Comment ID is required";
    public static final String VALIDATION_COMMENT_CONTENT_REQUIRED = "Comment content is required";
    public static final String VALIDATION_COMMENT_TYPE_REQUIRED = "Comment type is required";

}