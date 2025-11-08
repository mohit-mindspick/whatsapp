package com.assetneuron.whatsapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistItemDTO {
    
    private Integer sequence;
    private UUID id;
    private String description;
    private String instruction;
    private String inputType;
}

