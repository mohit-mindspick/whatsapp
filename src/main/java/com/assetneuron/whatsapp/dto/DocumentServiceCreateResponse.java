package com.assetneuron.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO from document service after creating documents
 * Response structure: { "data": { "documents": [...] } }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentServiceCreateResponse {
    
    @JsonProperty(value = "documents")
    private List<DocumentInfo> documents;
    
    /**
     * Inner DTO for each document in the response
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentInfo {
        
        @JsonProperty(value = "name")
        private String name;
        
        @JsonProperty(value = "uuid")
        private UUID uuid;
        
        @JsonProperty(value = "parentId")
        private UUID parentId;
        
        @JsonProperty(value = "parentType")
        private String parentType;
    }
}

