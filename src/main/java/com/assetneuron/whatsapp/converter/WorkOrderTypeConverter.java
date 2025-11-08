package com.assetneuron.whatsapp.converter;

import com.assetneuron.whatsapp.enums.WorkOrderType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class WorkOrderTypeConverter implements AttributeConverter<WorkOrderType, String> {

    @Override
    public String convertToDatabaseColumn(WorkOrderType attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public WorkOrderType convertToEntityAttribute(String dbData) {
        return dbData != null ? WorkOrderType.fromCode(dbData) : null;
    }
}

