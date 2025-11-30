package com.assetneuron.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    
    @JsonProperty(value = "id")
    private UUID id;
    
    @JsonProperty(value = "name")
    private String name;
    
    @JsonProperty(value = "url")
    private String url;
    
    @JsonProperty(value = "file_type")
    private String fileType;
    
    @JsonProperty(value = "file_size")
    private Long fileSize;
}

