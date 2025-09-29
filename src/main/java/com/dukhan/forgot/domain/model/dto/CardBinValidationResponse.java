package com.dukhan.forgot.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardBinValidationResponse {
    
    private boolean valid;
    private String message;
    private String bin;
    private String productType;
    private String cardType;
    private String code;
    
    public static CardBinValidationResponse success(String bin, String productType, String cardType, String code) {
        return new CardBinValidationResponse(true, "Card is valid", bin, productType, cardType, code);
    }
    
    public static CardBinValidationResponse invalid(String message) {
        return new CardBinValidationResponse(false, message, null, null, null, null);
    }
}
