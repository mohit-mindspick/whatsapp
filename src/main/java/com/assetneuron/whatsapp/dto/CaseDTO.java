package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.enums.SeverityLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseDTO {
    
    private UUID id;
    private String code;
    private String title;
    private SeverityLevel severity;
}

