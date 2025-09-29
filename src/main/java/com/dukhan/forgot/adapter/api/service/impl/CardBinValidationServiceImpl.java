package com.dukhan.forgot.adapter.api.service.impl;

import com.dukhan.forgot.adapter.api.service.CardBinValidationService;
import com.dukhan.forgot.domain.model.dto.CardBinValidationRequest;
import com.dukhan.forgot.domain.model.dto.CardBinValidationResponse;
import com.dukhan.forgot.domain.model.entity.CardBinMaster;
import com.dukhan.forgot.domain.repository.CardBinMasterRepository;
import com.dukhan.forgot.infrastructure.common.AppConstant;
import com.dukhan.forgot.infrastructure.common.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CardBinValidationServiceImpl implements CardBinValidationService {
    
    @Autowired
    private CardBinMasterRepository cardBinMasterRepository;
    
    @Override
    public GenericResponse<CardBinValidationResponse> validateCardBin(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, CardBinValidationRequest request) {
        try {
            String cardNumber = request.getCardNumber();
            String bin = extractBin(cardNumber);
            List<CardBinMaster> cardBinMasterList = cardBinMasterRepository.findByBin(bin);
            if (!cardBinMasterList.isEmpty()) {
                CardBinMaster cardBin = cardBinMasterList.get(0);
                CardBinValidationResponse response = CardBinValidationResponse.success(
                        cardBin.getBin(),
                        cardBin.getProductType(),
                        cardBin.getCardType(),
                        cardBin.getCode()
                );
                return GenericResponse.success(response);
            }
            
            return GenericResponse.error(AppConstant.GEN_ERROR_CODE, "Card not valid");
            
        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponse.error(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC);
        }
    }
    
    
    private String extractBin(String cardNumber) {
        if (cardNumber.length() >= 6) {
            return cardNumber.substring(0, 6);
        } else {
            return cardNumber;
        }
    }
}
