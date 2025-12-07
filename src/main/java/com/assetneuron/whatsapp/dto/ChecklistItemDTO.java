package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.common.constant.ErrorMessages;
import com.assetneuron.whatsapp.enums.ItemType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @JsonProperty(value = "id")
    @NotNull(message = ErrorMessages.VALIDATION_CHECKLIST_ITEM_ID_REQUIRED)
    private UUID id;

    @JsonProperty(value = "item_text")
    @NotBlank(message = ErrorMessages.VALIDATION_ITEM_TEXT_REQUIRED)
    private String itemText;

    @JsonProperty(value = "comment_id")
    private UUID commentId;

    @JsonProperty(value = "type")
    @NotNull(message = ErrorMessages.VALIDATION_ITEM_TYPE_REQUIRED)
    private ItemType type;

    @JsonProperty(value = "possible_options")
    private String possibleOptions;

}

