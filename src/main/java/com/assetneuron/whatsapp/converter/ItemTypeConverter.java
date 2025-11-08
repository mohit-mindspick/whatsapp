package com.assetneuron.whatsapp.converter;

import com.assetneuron.whatsapp.enums.ItemType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ItemTypeConverter implements AttributeConverter<ItemType, String> {

    @Override
    public String convertToDatabaseColumn(ItemType itemType) {
        if (itemType == null) {
            return null;
        }
        return itemType.name();
    }

    @Override
    public ItemType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return ItemType.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
