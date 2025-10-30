package com.dukhan.forgot.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpGenerateRequest {
    
    @NotNull(message = "Request info is required")
    @Valid
    private RequestInfo requestInfo;
    
    @NotNull(message = "Device info is required")
    @Valid
    private DeviceInfo deviceInfo;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestInfo {
        @NotBlank(message = "Action is required")
        private String action;
        
        @NotBlank(message = "Customer number is required")
        private String customerId;
    }
}
