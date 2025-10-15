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
    private String cardNumber;

    @NotBlank
    private String pin;
}
