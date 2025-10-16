package com.dukhan.forgot.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardBinValidationWrapper {

    @Valid
    private CardBinValidationRequest requestInfo;
    
    @Valid
    @NotNull(message = "Device information is required")
    private DeviceInfo deviceInfo;
}