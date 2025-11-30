package com.assetneuron.whatsapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating comment in the comment service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentServiceCreateRequest {

    @NotBlank(message = "Content is required")
    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    private String content;

    @Size(max = 255, message = "Parent ID must not exceed 255 characters")
    private String parentId;

    @NotBlank(message = "Parent type is required")
    @Size(max = 100, message = "Parent type must not exceed 100 characters")
    private String parentType;

    @Size(max = 100, message = "Source service must not exceed 100 characters")
    private String sourceService;

    @Size(max = 255, message = "Source object must not exceed 255 characters")
    private String sourceObject;

    // If this is a reply to another comment
    private UUID replyToCommentId;

    // List of document IDs associated with this comment
    private List<String> documentList;

}

