package com.dukhan.forgot.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardBinValidationRequest {

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{16}", message = "Card number must be exactly 16 digits")
    private String cardNumber;

    @NotBlank(message = "Card pin is required")
    @Pattern(regexp = "\\d{4}", message = "Card pin must be exactly 4 digits")
    private String pin;
}
