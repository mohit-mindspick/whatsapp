package com.assetneuron.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating documents in the document service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentServiceCreateRequest {

    @NotEmpty(message = "Documents list cannot be empty")
    @Valid
    private List<ExternalDocument> documents;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalDocument {

        @NotBlank(message = "URL is required")
        @Size(max = 2048, message = "URL must not exceed 2048 characters")
        private String url;

        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name must not exceed 255 characters")
        private String name;

        @NotBlank(message = "MIME type is required")
        @JsonProperty(value = "mime_type")
        @Size(max = 100, message = "MIME type must not exceed 100 characters")
        private String mimeType;

        @NotBlank(message = "Parent type is required")
        @JsonProperty(value = "parent_type")
        @Size(max = 100, message = "Parent type must not exceed 100 characters")
        private String parentType;

        @NotNull(message = "Parent id is required")
        @JsonProperty(value = "parent_id")
        private UUID parentId;
    }
}

