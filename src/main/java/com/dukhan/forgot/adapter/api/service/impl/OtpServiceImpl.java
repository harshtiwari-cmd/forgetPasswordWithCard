package com.dukhan.forgot.adapter.api.service.impl;

import com.dukhan.forgot.adapter.api.service.OtpService;
import com.dukhan.forgot.domain.model.dto.OtpGenerateRequest;
import com.dukhan.forgot.domain.model.dto.OtpGenerateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class OtpServiceImpl implements OtpService {
    
    private static final Logger logger = LoggerFactory.getLogger(OtpServiceImpl.class);
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${otp.service.url}")
    private String otpServiceUrl;
    
    @Override
    public OtpGenerateResponse generateOtp(String unit, String channel, String lang, String serviceId, 
                                          String screenId, String moduleId, String subModuleId, 
                                          OtpGenerateRequest request) {
        try {
            logger.debug("Calling OTP generation API for customerId: {}",
                    request.getRequestInfo().getRimNo());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("unit", unit != null ? unit : "DEFAULT");
            headers.set("channel", channel != null ? channel : "WEB");
            headers.set("accept-language", lang != null ? lang : "en");
            headers.set("serviceId", serviceId != null ? serviceId : "OTP_SERVICE");
            headers.set("screenId", screenId != null ? screenId : "LOGIN_SCREEN");
            headers.set("moduleId", moduleId != null ? moduleId : "AUTH_MODULE");
            headers.set("subModuleId", subModuleId != null ? subModuleId : "OTP_SUBMODULE");

            HttpEntity<OtpGenerateRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<OtpGenerateResponse> response = restTemplate.exchange(
                    otpServiceUrl,
                    HttpMethod.POST,
                    entity,
                    OtpGenerateResponse.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                OtpGenerateResponse otpResponse = response.getBody();
                logger.info("OTP generation successful for customerId: {}, status: {}",
                        request.getRequestInfo().getRimNo(),
                        otpResponse.getStatus() != null ? otpResponse.getStatus().getDescription() : "UNKNOWN");
                return otpResponse;
            } else {
                logger.warn("OTP generation failed - HTTP Status: {}, Response: {}", 
                        response.getStatusCode(), response.getBody());
                return createFailureResponse("OTP generation failed");
            }
            
        } catch (Exception e) {
            logger.error("Exception occurred while calling OTP generation API: {}", e.getMessage(), e);
            return createFailureResponse("OTP generation service unavailable");
        }
    }
    
    private OtpGenerateResponse createFailureResponse(String message) {
        return OtpGenerateResponse.builder()
                .status(OtpGenerateResponse.Status.builder()
                        .code("999999")
                        .description("FAILED")
                        .build())
                .data(OtpGenerateResponse.OtpData.builder()
                        .message(message)
                        .build())
                .build();
    }
}
