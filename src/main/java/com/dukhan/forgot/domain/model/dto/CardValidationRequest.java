package com.dukhan.forgot.domain.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardValidationRequest {
    @NotBlank
    private String cardNumber;

    @NotBlank
    private String pin;

}
