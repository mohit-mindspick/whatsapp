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
public class PartDTO {
    
    private UUID partsId;
    private String partsName;
    private Integer quantity;
    private String status;
}

