package com.dukhan.forgot.adapter.api.controller;

import com.dukhan.forgot.adapter.api.service.CardBinValidationService;
import com.dukhan.forgot.domain.model.dto.*;
import com.dukhan.forgot.infrastructure.common.AppConstant;
import com.dukhan.forgot.infrastructure.common.GenericResponse;
import com.dukhan.forgot.domain.model.entity.CardBinMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Collections;

@RestController
@Validated
public class CardBinValidationController {
    
    private static final Logger logger = LoggerFactory.getLogger(CardBinValidationController.class);
    
    private final CardBinValidationService cardBinValidationService;

    public CardBinValidationController(CardBinValidationService cardBinValidationService) {
        this.cardBinValidationService = cardBinValidationService;
    }

    @PostMapping("/validate")
    public ResponseEntity<GenericResponse<SimpleValidationResponse>> validateCardBin(
            @RequestHeader(name = AppConstant.UNIT, required = true) String unit,
            @RequestHeader(name = AppConstant.HEADER_CHANNEL, required = true) String channel,
            @RequestHeader(name = AppConstant.HEADER_ACCEPT_LANGUAGE, required = true) String lang,
            @RequestHeader(name = AppConstant.SERVICEID, required = true) String serviceId,
            @RequestHeader(name = AppConstant.SCREEN_ID, required = true) String screenId,
            @RequestHeader(name = AppConstant.MODULE_ID, required = true) String moduleId,
            @RequestHeader(name = AppConstant.SUB_MODULE_ID, required = true) String subModuleId,
            @Valid @RequestBody CardBinValidationWrapper wrapper) {

           CardBinValidationRequest request = wrapper.getRequestInfo();

           logger.info("CardBin validation and PIN encryption request received - Unit: {}, Channel: {}, ServiceId: {}, CardNumber: {}",
                unit, channel, serviceId, maskCardNumber(request.getCardNumber()));
        
        try {
            GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                    unit, channel, lang, serviceId, screenId, moduleId, subModuleId, request);
            
            if (response == null || response.getStatus() == null || !AppConstant.RESULT_CODE.equals(response.getStatus().getCode())) {
                return ResponseEntity.ok(GenericResponse.error(AppConstant.VALIDATION_FAILURE_CODE, AppConstant.VALIDATION_FAILURE_DESC));
            }
            SimpleValidationResponse data = response.getData();
            if (data == null || (data.getRimNumber() == null && data.getUserName() == null && !data.isOtp())) {
                return ResponseEntity.ok(GenericResponse.error(AppConstant.VALIDATION_FAILURE_CODE, AppConstant.VALIDATION_FAILURE_DESC));
            }
            return ResponseEntity.ok(GenericResponse.success(data));
        } catch (Exception e) {
            logger.error("Error occurred during CardBin validation and PIN encryption - Unit: {}, Channel: {}, ServiceId: {}, Error: {}",
                    unit, channel, serviceId, e.getMessage(), e);
            return ResponseEntity.ok(GenericResponse.error(AppConstant.VALIDATION_FAILURE_CODE, AppConstant.VALIDATION_FAILURE_DESC));
        }
    }
    
    @PostMapping("/bin-details")
    public GenericResponse<java.util.List<CardBinMaster>> getActiveBins(
            @RequestHeader(name = AppConstant.SERVICEID, required = true) String serviceId,
            @RequestHeader(name = AppConstant.MODULE_ID, required = true) String moduleId,
            @RequestHeader(name = AppConstant.SUB_MODULE_ID, required = true) String subModuleId,
            @RequestHeader(name = AppConstant.SCREEN_ID, required = true) String screenId,
            @RequestHeader(name = AppConstant.HEADER_CHANNEL, required = true) String channel,
            @RequestHeader(name = AppConstant.HEADER_ACCEPT_LANGUAGE, required = true) String lang,
            @Valid @RequestBody(required = true) CardBinAllWrapper wrapper) {
        
        if (wrapper == null) {
            logger.error("Request body is null - ServiceId: {}, ModuleId: {}", serviceId, moduleId);
            return GenericResponse.error(AppConstant.GEN_ERROR_CODE, "Request body is required");
        }
        
        if (wrapper.getDeviceInfo() == null) {
            logger.error("Device information is null - ServiceId: {}, ModuleId: {}", serviceId, moduleId);
            return GenericResponse.error(AppConstant.GEN_ERROR_CODE, "Device information is required");
        }
        
        logger.info("Request received to fetch active CardBin records - ServiceId: {}, ModuleId: {}, SubModuleId: {}, ScreenId: {}, Channel: {}, DeviceId: {}", 
                serviceId, moduleId, subModuleId, screenId, channel, wrapper.getDeviceInfo().getDeviceId());
        try {
            GenericResponse<java.util.List<CardBinMaster>> response = cardBinValidationService.getActiveBins();
            if (response == null || response.getStatus() == null || !AppConstant.RESULT_CODE.equals(response.getStatus().getCode())) {
                logger.error("Service returned error response for getActiveBins");
                return GenericResponse.error(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC);
            }
            
            if (response.getData() == null || response.getData().isEmpty()) {
                logger.info("No active CardBin records found");
                return GenericResponse.successNoData(Collections.emptyList());
            }
            logger.info("Fetched active CardBin records - count: {}", response.getData().size());
            return GenericResponse.success(response.getData());
        } catch (Exception e) {
            logger.error("Error occurred while fetching active CardBin records: {}", e.getMessage(), e);
            return GenericResponse.error(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC);
        }
    }

    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            return "****";
        }
        return cardNumber.substring(0, 4) + "****" + cardNumber.substring(cardNumber.length() - 4);
    }
    
}
