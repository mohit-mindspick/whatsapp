package com.assetneuron.whatsapp.converter;

import com.assetneuron.whatsapp.enums.WorkOrderStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class WorkOrderStatusConverter implements AttributeConverter<WorkOrderStatus, String> {

    @Override
    public String convertToDatabaseColumn(WorkOrderStatus workOrderStatus) {
        if (workOrderStatus == null) {
            return null;
        }
        return workOrderStatus.name();
    }

    @Override
    public WorkOrderStatus convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return WorkOrderStatus.fromCode(code);
    }
}
