package com.dukhan.forgot.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpGenerateResponse {
    private Status status;
    private OtpData data;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Status {
        private String code;
        private String description;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OtpData {
        private String mobileNumber;
        private String message;
    }
}
