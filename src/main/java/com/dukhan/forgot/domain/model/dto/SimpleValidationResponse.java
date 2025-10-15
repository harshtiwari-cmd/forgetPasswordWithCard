package com.dukhan.forgot.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleValidationResponse {
    
    private String rimNumber;
    private boolean otp;
    

}
