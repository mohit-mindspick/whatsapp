package com.assetneuron.whatsapp.common.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseEvent {
    private String eventId;
    private EventType eventType;
    private String entityType;
    private UUID entityId;
    private String correlationId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;
    
    private String source;
    private String version;
    
    // Generic event data - can hold any entity data
    private Object eventData;
    
    // Previous event data for UPDATE events
    private Object previousEventData;
}

