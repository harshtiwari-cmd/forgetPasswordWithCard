package com.dukhan.forgot.adapter.api.controller;

import com.dukhan.forgot.adapter.api.service.CardBinValidationService;
import com.dukhan.forgot.domain.model.dto.CardBinValidationRequest;
import com.dukhan.forgot.domain.model.dto.CardBinValidationResponse;
import com.dukhan.forgot.infrastructure.common.AppConstant;
import com.dukhan.forgot.infrastructure.common.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v2/card-bin")
@Validated
public class CardBinValidationController {
    
    @Autowired
    private CardBinValidationService cardBinValidationService;
    
    @PostMapping("/validate")
    public GenericResponse<CardBinValidationResponse> validateCardBin(
            @RequestHeader(name = AppConstant.UNIT, required = false) String unit,
            @RequestHeader(name = AppConstant.HEADER_CHANNEL, required = false) String channel,
            @RequestHeader(name = AppConstant.HEADER_ACCEPT_LANGUAGE, required = false) String lang,
            @RequestHeader(name = AppConstant.SERVICEID, required = false) String serviceId,
            @RequestHeader(name = AppConstant.SCREEN_ID, required = false) String screenId,
            @RequestHeader(name = AppConstant.MODULE_ID, required = false) String moduleId,
            @RequestHeader(name = AppConstant.SUB_MODULE_ID, required = false) String subModuleId,
            @Valid @RequestBody CardBinValidationRequest request) {
        
        return cardBinValidationService.validateCardBin(unit, channel, lang, serviceId, screenId, moduleId, subModuleId, request);
    }
    
}
