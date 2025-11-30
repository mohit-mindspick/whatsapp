package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.common.constant.ErrorMessages;
import com.assetneuron.whatsapp.enums.DateFilterType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewMyWorkRequest {
    
    @JsonProperty(value = "phone_number")
    @NotBlank(message = ErrorMessages.VALIDATION_PHONE_NUMBER_REQUIRED)
    private String phoneNumber;
    
    @JsonProperty(value = "date_filter")
    @NotNull(message = ErrorMessages.VALIDATION_DATE_FILTER_REQUIRED)
    private DateFilterType dateFilter;
}

