package com.dukhan.forgot.adapter.api.service;

import com.dukhan.forgot.domain.model.dto.CardBinValidationRequest;
import com.dukhan.forgot.domain.model.dto.SimpleValidationResponse;
import com.dukhan.forgot.infrastructure.common.GenericResponse;

public interface CardBinValidationService {
    
    GenericResponse<SimpleValidationResponse> validateCardBin(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, CardBinValidationRequest request);

    GenericResponse<java.util.List<com.dukhan.forgot.domain.model.entity.CardBinMaster>> getActiveBins();
}
