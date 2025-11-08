package com.assetneuron.whatsapp.dto;

import com.assetneuron.whatsapp.common.constant.ErrorMessages;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserByPhoneRequest {
    
    @JsonProperty(value = "phone_number")
    @NotBlank(message = ErrorMessages.VALIDATION_PHONE_NUMBER_REQUIRED)
    private String phoneNumber;
}

