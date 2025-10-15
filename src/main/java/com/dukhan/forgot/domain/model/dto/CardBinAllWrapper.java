package com.dukhan.forgot.domain.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardBinAllWrapper {
    @Valid
    private Object requestInfo;

    @Valid
    @NotNull(message = "Device information is required")
    private DeviceInfo deviceInfo;
}
