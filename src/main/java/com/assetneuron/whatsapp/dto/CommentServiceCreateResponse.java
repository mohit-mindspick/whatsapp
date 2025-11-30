package com.assetneuron.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO from comment service after creating comment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentServiceCreateResponse {
    
    @JsonProperty(value = "id")
    private UUID id;
    
    @JsonProperty(value = "content")
    private String content;
    
    @JsonProperty(value = "parentId")
    private String parentId;
    
    @JsonProperty(value = "parentType")
    private String parentType;
    
    @JsonProperty(value = "sourceService")
    private String sourceService;
    
    @JsonProperty(value = "sourceObject")
    private String sourceObject;
}

