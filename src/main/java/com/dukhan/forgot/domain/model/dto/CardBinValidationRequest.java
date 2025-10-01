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
    @Pattern(regexp = "^\\d{15,}$", message = "Card number must be at least 15 digits")
    private String cardNumber;
}
