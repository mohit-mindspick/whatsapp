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
public class TaskDTO {
    
    private UUID taskId;
    private Integer sequence;
    private String name;
    private String instruction;
    private Integer duration;

}

