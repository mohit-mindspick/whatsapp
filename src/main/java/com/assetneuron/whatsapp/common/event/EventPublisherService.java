package com.assetneuron.whatsapp.common.event;

import com.assetneuron.whatsapp.common.util.CorrelationIdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisherService {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    public void publishEvent(String topic, String entityType, EventType eventType, 
                           UUID entityId, Object eventData, Object previousEventData) {
        try {
            BaseEvent event = createBaseEvent(entityType, eventType, entityId, eventData, previousEventData);
            String eventJson = objectMapper.writeValueAsString(event);
            
            kafkaTemplate.send(topic, eventJson);
            log.info("Event published successfully: {} for entity {} with ID {}", 
                    eventType, entityType, entityId);
        } catch (Exception e) {
            log.error("Failed to publish event: {} for entity {} with ID {}", 
                     eventType, entityType, entityId, e);
        }
    }
    
    public void publishEvent(String topic, String entityType, EventType eventType, 
                           UUID entityId, Object eventData) {
        publishEvent(topic, entityType, eventType, entityId, eventData, null);
    }
    
    private BaseEvent createBaseEvent(String entityType, EventType eventType, 
                                    UUID entityId, Object eventData, Object previousEventData) {
        BaseEvent event = new BaseEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(eventType);
        event.setEntityType(entityType);
        event.setEntityId(entityId);
        event.setCorrelationId(CorrelationIdUtil.getCorrelationId());
        event.setTimestamp(LocalDateTime.now());
        event.setSource("whatsapp-service");
        event.setVersion("1.0");
        event.setEventData(eventData);
        event.setPreviousEventData(previousEventData);
        
        return event;
    }
}

