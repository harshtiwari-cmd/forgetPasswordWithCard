package com.dukhan.forgot.adapter.api.controller;

import com.dukhan.forgot.adapter.api.service.CardBinValidationService;
import com.dukhan.forgot.domain.model.dto.CardBinValidationRequest;
import com.dukhan.forgot.domain.model.dto.CardBinValidationResponse;
import com.dukhan.forgot.domain.model.dto.CardBinValidationWrapper;
import com.dukhan.forgot.infrastructure.common.AppConstant;
import com.dukhan.forgot.infrastructure.common.GenericResponse;
import com.dukhan.forgot.domain.model.entity.CardBinMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("/v2/card-bin")
@Validated
public class CardBinValidationController {
    
    private static final Logger logger = LoggerFactory.getLogger(CardBinValidationController.class);

    
    private final CardBinValidationService cardBinValidationService;

    public CardBinValidationController(CardBinValidationService cardBinValidationService) {
        this.cardBinValidationService = cardBinValidationService;
    }

    @PostMapping("/validate")
    public GenericResponse<CardBinValidationResponse> validateCardBin(
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
            GenericResponse<CardBinValidationResponse> response = cardBinValidationService.validateCardBin(
                    unit, channel, lang, serviceId, screenId, moduleId, subModuleId, request);
            
            boolean isSuccess = AppConstant.RESULT_CODE.equals(response.getStatus().getCode());
            logger.info("CardBin validation and PIN encryption completed - Unit: {}, Channel: {}, ServiceId: {}, Success: {}, HasEncryptedPin: {}",
                    unit, channel, serviceId, isSuccess, 
                    isSuccess && response.getData() != null && response.getData().getEncryptedPin() != null);
            
            return response;
        } catch (Exception e) {
            logger.error("Error occurred during CardBin validation and PIN encryption - Unit: {}, Channel: {}, ServiceId: {}, Error: {}",
                    unit, channel, serviceId, e.getMessage(), e);
            throw e;
        }
    }
    
    @GetMapping("/active")
    public GenericResponse<java.util.List<CardBinMaster>> getActiveBins() {
        logger.info("Request received to fetch active CardBin records");
        try {
            GenericResponse<java.util.List<CardBinMaster>> response = cardBinValidationService.getActiveBins();
            if (response == null || response.getData() == null || response.getData().isEmpty()) {
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

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            return "****";
        }
        return cardNumber.substring(0, 4) + "****" + cardNumber.substring(cardNumber.length() - 4);
    }
    
}
