package com.dukhan.forgot.adapter.api.service.impl;

import com.dukhan.forgot.adapter.api.service.CardBinValidationService;
import com.dukhan.forgot.domain.model.dto.CardBinValidationRequest;
import com.dukhan.forgot.domain.model.dto.CardBinValidationResponse;
import com.dukhan.forgot.domain.model.entity.CardBinMaster;
import com.dukhan.forgot.domain.repository.CardBinMasterRepository;
import com.dukhan.forgot.infrastructure.common.AppConstant;
import com.dukhan.forgot.infrastructure.common.GenericResponse;
import com.dukhan.forgot.infrastructure.common.exception.BARWAHSMEncryptionException;
import com.dukhan.forgot.infrastructure.common.exception.BARWAHSMParsingException;
import com.dukhan.forgot.infrastructure.common.exception.BarwaHSMCommuicationException;
import com.dukhan.forgot.infrastructure.common.hsm.HSMEncryptorManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CardBinValidationServiceImpl implements CardBinValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CardBinValidationServiceImpl.class);
    
    private CardBinMasterRepository cardBinMasterRepository;
    private final HSMEncryptorManagerImpl hsmEncryptor;

    public CardBinValidationServiceImpl(HSMEncryptorManagerImpl hsmEncryptor,CardBinMasterRepository cardBinMasterRepository) {
        this.hsmEncryptor = hsmEncryptor;
        this.cardBinMasterRepository = cardBinMasterRepository;
    }
    @Override
    public GenericResponse<CardBinValidationResponse> validateCardBin(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, CardBinValidationRequest request) {
        logger.debug("Starting CardBin validation for unit: {}, channel: {}, serviceId: {}", unit, channel, serviceId);

        try {
            String cardNumber = request.getCardNumber();
            String pin = request.getPin();
            
            CardBinMaster matchedBin = findMatchingBin(cardNumber);

            if (matchedBin == null) {
                logger.warn("Card validation failed - No CardBin record found for card number: {}", cardNumber);
                return GenericResponse.error(AppConstant.GEN_ERROR_CODE, "Card not valid");
            }

            logger.info("Card BIN validation successful - BIN: {}, ProductType: {}, CardType: {}, Code: {}",
                    matchedBin.getBin(), matchedBin.getProductType(), matchedBin.getCardType(), matchedBin.getCode());

            String encryptedPin;
            try {
                encryptedPin = hsmEncryptor.generatePinBlockUnderZPK(pin, cardNumber, "CardBinValidation");
                logger.info("PIN encryption successful for card: {}", cardNumber);
            } catch (BarwaHSMCommuicationException | BARWAHSMEncryptionException | BARWAHSMParsingException e) {
                logger.error("HSM encryption failed for card: {}, error: {}", cardNumber, e.getMessage(), e);
                return GenericResponse.error("HSM-001", "PIN encryption failed: " + e.getMessage());
            }
//-------REQUEST XML CREATION AND CALL MQ if isMOCKREPSONSE is false-----
//            if true so we need to set static xml response:
            String xmlResponse="eAI_MESSAGE\n" +
                    "eAI_HEADER\n" +
                    "serviceName\n" +
                    "__prefix NS1\n" +
                    "__text DCARD.PIN.VERIFICATION\n" +
                    "toString function(){return(null!=this.__text?this.__text:\"\")+(null!=this.__cdata?this.__cdata:\"\")}\n" +
                    "serviceType\n" +
                    "__prefix NS1\n" +
                    "__text SYNC\n" +
                    "toString function(){return(null!=this.__text?this.__text:\"\")+(null!=this.__cdata?this.__cdata:\"\")}\n" +
                    "serviceVersion\n" +
                    "__prefix NS1\n" +
                    "__text 1\n" +
                    "toString function(){return(null!=this.__text?this.__text:\"\")+(null!=this.__cdata?this.__cdata:\"\")}\n" +
                    "client\n" +
                    "__prefix NS1\n" +
                    "__text BKR\n" +
                    "toString function(){return(null!=this.__text?this.__text:\"\")+(null!=this.__cdata?this.__cdata:\"\")}\n" +
                    "clientChannel\n" +
                    "__prefix NS1\n" +
                    "__text MOB\n" +
                    "toString function(){return(null!=this.__text?this.__text:\"\")+(null!=this.__cdata?this.__cdata:\"\")}\n" +
                    "msgChannel\n" +
                    "__prefix NS1\n" +
                    "__text MQ\n" +
                    "toString function(){return(null!=this.__text?this.__text:\"\")+(null!=this.__cdata?this.__cdata:\"\")}\n" +
                    "requestorLanguage\n" +
                    "__prefix NS1\n" +
                    "__text E\n" +
                    "toString function(){return(null!=this.__text?this.__text:\"\")+(null!=this.__cdata?this.__cdata:\"\")}\n" +
                    "securityInfo\n" +
                    "authentication\n" +
                    "UserId\n" +
                    "__prefix NS1\n" +
                    "__text NS1:UserId\n" +
                    "toString function(){return(null!=this.__text?this.__text:\"\")+(null!=this.__cdata?this.__cdata:\"\")}\n" +
                    "Password\n" +
                    "__prefix NS1\n" +
                    "__text NS1:Password\n" +
                    "toString function(){return(null!=this.__text?this.__text:\"\")+(null!=this.__cdata?this.__cdata:\"\")}\n" +
                    "__prefix NS1\n" +
                    "authorization\n" +
                    "UserId\n" +
                    "__prefix NS1\n" +
                    "__text NS1:UserId\n" +
                    "toString function(){return(null!=this.__text?this.__text:\"\")+(null!=this.__cdata?this.__cdata:\"\")}\n" +
                    "__prefix NS1\n" +
                    "__prefix NS1\n" +
                    "returnCode\n" +
                    "__prefix NS1\n" +
                    "__text 0000\n" +
                    "toString function(){return(null!=this.__text?this.__text:\"\")+(null!=this.__cdata?this.__cdata:\"\")}\n" +
                    "__prefix NS1\n" +
                    "eAI_BODY\n" +
                    "eAI_REPLY\n" +
                    "debitCardPINVerificationReply\n" +
                    "referenceNum\n" +
                    "__prefix NS1\n" +
                    "__text TAM650\n" +
                    "toString function(){return(null!=this.__text?this.__text:\"\")+(null!=this.__cdata?this.__cdata:\"\")}\n" +
                    "requestTime\n" +
                    "__prefix NS1\n" +
                    "__text 20130429233157568\n" +
                    "toString function(){return(null!=this.__text?this.__text:\"\")+(null!=this.__cdata?this.__cdata:\"\")}\n" +
                    "returnStatus\n" +
                    "returnCode\n" +
                    "__prefix NS1\n" +
                    "__text 0000\n" +
                    "toString function(){return(null!=this.__text?this.__text:\"\")+(null!=this.__cdata?this.__cdata:\"\")}\n" +
                    "returnCodeDesc\n" +
                    "__prefix NS1\n" +
                    "__text Success\n" +
                    "toString function(){return(null!=this.__text?this.__text:\"\")+(null!=this.__cdata?this.__cdata:\"\")}\n" +
                    "__prefix NS1\n" +
                    "returnStatusProvider\n" +
                    "returnCodeProvider\n" +
                    "__prefix NS1\n" +
                    "__text 0000\n" +
                    "toString function(){return(null!=this.__text?this.__text:\"\")+(null!=this.__cdata?this.__cdata:\"\")}\n" +
                    "returnCodeDescProvider\n" +
                    "__prefix NS1\n" +
                    "__text SUCCESS\n" +
                    "toString function(){return(null!=this.__text?this.__text:\"\")+(null!=this.__cdata?this.__cdata:\"\")}\n" +
                    "__prefix NS1\n" +
                    "__prefix NS1\n" +
                    "__prefix NS1\n" +
                    "__prefix NS1\n" +
                    "_xmlns:NS1 urn:esbbank.com/gbo/xml/schemas/v1_0/\n" +
                    "__prefix NS1\n";

//            Unmarshal code (XML to response)
            CardBinValidationResponse response = new CardBinValidationResponse(
                    true, 
                    "Card is valid and PIN encrypted successfully", 
                    matchedBin.getBin(),
                    matchedBin.getProductType(),
                    matchedBin.getCardType(),
                    matchedBin.getCode(),
                    encryptedPin
            );
            
            return GenericResponse.success(response);

        } catch (Exception e) {
            logger.error("Exception occurred during CardBin validation for unit: {}, channel: {}, serviceId: {}, error: {}",
                    unit, channel, serviceId, e.getMessage(), e);
            return GenericResponse.error(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC);
        }
    }

    @Override
    public GenericResponse<java.util.List<CardBinMaster>> getActiveBins() {
        logger.info("Fetching active CardBin records");
        try {
            List<CardBinMaster> active = cardBinMasterRepository.findAllActive();
            logger.debug("Active CardBin records found: {}", active != null ? active.size() : 0);
            return GenericResponse.success(active);
        } catch (Exception e) {
            logger.error("Exception occurred while fetching active CardBin records: {}", e.getMessage(), e);
            return GenericResponse.error(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC);
        }
    }

    private CardBinMaster findMatchingBin(String cardNumber) {
        logger.debug("Extracting BIN from card number of length: {}", cardNumber != null ? cardNumber.length() : 0);

        if (cardNumber == null) {
            logger.warn("Card number is null, cannot extract BIN");
            return null;
        }

        int[] binLengths = {8, 7, 6};
        for (int len : binLengths) {
            if (cardNumber.length() >= len) {
                String binCandidate = cardNumber.substring(0, len);
                List<CardBinMaster> binMasterList = cardBinMasterRepository.findByBin(binCandidate);
                logger.debug("Searched for BIN: {}, found {} records", binCandidate, binMasterList.size());
                if (!binMasterList.isEmpty()) {
                    return binMasterList.get(0);
                }
            }
        }

        logger.debug("No BIN match found for card number: {}", cardNumber);
        return null;
    }
}
