package com.dukhan.forgot.adapter.api.service.impl;

import com.dukhan.forgot.adapter.api.service.CardBinValidationService;
import com.dukhan.forgot.domain.model.dto.CardBinValidationRequest;
import com.dukhan.forgot.domain.model.dto.SimpleValidationResponse;
import com.dukhan.forgot.domain.model.entity.CardBinMaster;
import com.dukhan.forgot.infrastructure.common.GenericResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@ConditionalOnProperty(name = "mock.enabled", havingValue = "true")
public class MockCardBinValidationServiceImpl implements CardBinValidationService {

    private static final Logger logger = LoggerFactory.getLogger(MockCardBinValidationServiceImpl.class);
    
    private final ObjectMapper objectMapper;

    public MockCardBinValidationServiceImpl() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public GenericResponse<SimpleValidationResponse> validateCardBin(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, CardBinValidationRequest request) {
        logger.info("Mock CardBinValidationService.validateCardBin called with unit: {}, channel: {}, serviceId: {}", unit, channel, serviceId);
        
        try {
            ClassPathResource resource = new ClassPathResource("JSON/GenericResponse_SimpleValidationResponse.json");
            GenericResponse<SimpleValidationResponse> mockResponse = objectMapper.readValue(
                resource.getInputStream(), 
                new TypeReference<GenericResponse<SimpleValidationResponse>>() {}
            );
            
            logger.info("Mock response loaded successfully for validateCardBin");
            return mockResponse;
        } catch (IOException e) {
            logger.error("Error loading mock response for validateCardBin: {}", e.getMessage(), e);
            return GenericResponse.error("MOCK_ERROR", "Failed to load mock response");
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
