package com.dukhan.forgot.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.dukhan.forgot.infrastructure.common.xmlResponse.EAIMessage;
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
    private String encryptedPin;
    private String generatedXmlRequest;
    @JsonIgnore
    private String mockXmlResponse;
    private EAIMessage parsedXmlReply;
    
    public static CardBinValidationResponse success(String bin, String productType, String cardType, String code) {
        return new CardBinValidationResponse(true, "Card is valid", bin, productType, cardType, code, null, null, null, null);
    }
    
    public static CardBinValidationResponse invalid(String message) {
        return new CardBinValidationResponse(false, message, null, null, null, null, null, null, null, null);
    }
}
