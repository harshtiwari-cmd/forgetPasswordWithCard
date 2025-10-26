package com.dukhan.forgot.adapter.api.service.impl;

import com.dukhan.forgot.adapter.api.service.CardBinValidationService;
import com.dukhan.forgot.domain.model.dto.CardBinValidationRequest;
import com.dukhan.forgot.domain.model.dto.DeviceInfo;
import com.dukhan.forgot.domain.model.dto.SimpleValidationResponse;
import com.dukhan.forgot.domain.model.entity.CardBinMaster;
import com.dukhan.forgot.infrastructure.common.GenericResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@ConditionalOnProperty(name = "mock.enabled", havingValue = "true")
public class MockCardBinValidationServiceImpl implements CardBinValidationService {

    private static final Logger logger = LoggerFactory.getLogger(MockCardBinValidationServiceImpl.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public GenericResponse<SimpleValidationResponse> validateCardBin(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, CardBinValidationRequest request, DeviceInfo deviceInfo) {
        logger.info("Mock CardBinValidationService.validateCardBin called with unit: {}, channel: {}, serviceId: {}", unit, channel, serviceId);
        
        String cardNumber = request.getCardNumber();
        String mockResponseFile = getMockResponseFile(cardNumber);
        
        try {
            ClassPathResource resource = new ClassPathResource(mockResponseFile);
            GenericResponse<SimpleValidationResponse> mockResponse = objectMapper.readValue(
                resource.getInputStream(), 
                new TypeReference<GenericResponse<SimpleValidationResponse>>() {}
            );
            
            logger.info("Mock response loaded successfully for validateCardBin with card: {}", cardNumber);
            return mockResponse;
        } catch (IOException e) {
            logger.error("Error loading mock response for validateCardBin: {}", e.getMessage(), e);
            return GenericResponse.error("MOCK_ERROR", "Failed to load mock response");
        }
    }

    private String getMockResponseFile(String cardNumber) {
        if (cardNumber == null) {
            return "JSON/GenericResponse_SimpleValidationResponse.json";
        }
        
        switch (cardNumber) {
            case "4203741234567889":
                return "JSON/GenericResponse_SimpleValidationResponse_Positive.json";
            case "3209741234567889":
                return "JSON/GenericResponse_SimpleValidationResponse_BinNotValid.json";
            case "1003741234567889":
                return "JSON/GenericResponse_SimpleValidationResponse_CardNotValid.json";
            case "9003901234567889":
                return "JSON/GenericResponse_SimpleValidationResponse_UserBlocked.json";
            case "9898741234567889":
                return "JSON/GenericResponse_SimpleValidationResponse_OtpLimitExceeded.json";
            case "8080741234567889":
                return "JSON/GenericResponse_SimpleValidationResponse_InvalidAttempts.json";
            case "6060741234567889":
                return "JSON/GenericResponse_SimpleValidationResponse_RetryAfter24Hours.json";
            default:
                return "JSON/GenericResponse_SimpleValidationResponse_Positive.json";
        }
    }

    @Override
    public GenericResponse<List<CardBinMaster>> getActiveBins() {
        logger.info("Mock CardBinValidationService.getActiveBins called");
        
        try {
            ClassPathResource resource = new ClassPathResource("JSON/GenericResponse_CardBinMasterList.json");
            GenericResponse<List<CardBinMaster>> mockResponse = objectMapper.readValue(
                resource.getInputStream(), 
                new TypeReference<GenericResponse<List<CardBinMaster>>>() {}
            );
            
            logger.info("Mock response loaded successfully for getActiveBins");
            return mockResponse;
        } catch (IOException e) {
            logger.error("Error loading mock response for getActiveBins: {}", e.getMessage(), e);
            return GenericResponse.error("MOCK_ERROR", "Failed to load mock response");
        }
    }
}
