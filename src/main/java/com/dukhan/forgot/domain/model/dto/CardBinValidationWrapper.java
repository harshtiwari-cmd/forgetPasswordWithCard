package com.dukhan.forgot.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardBinValidationWrapper {

    private CardBinValidationRequest requestInfo;
    private DeviceInfo deviceInfo;
}