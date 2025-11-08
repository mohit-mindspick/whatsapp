package com.assetneuron.whatsapp.converter;

import com.assetneuron.whatsapp.enums.WorkOrderPartStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class WorkOrderPartStatusConverter implements AttributeConverter<WorkOrderPartStatus, String> {

    @Override
    public String convertToDatabaseColumn(WorkOrderPartStatus workOrderPartStatus) {
        if (workOrderPartStatus == null) {
            return null;
        }
        return workOrderPartStatus.name();
    }

    @Override
    public WorkOrderPartStatus convertToEntityAttribute(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (WorkOrderPartStatus status : WorkOrderPartStatus.values()) {
            if (status.name().equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid WorkOrderPartStatus code: " + code);
    }
}
