package com.dukhan.forgot.adapter.api.service.impl;

import com.dukhan.forgot.adapter.api.service.BankMiddlewareService;
import com.dukhan.forgot.adapter.api.service.CardBinValidationService;
import com.dukhan.forgot.adapter.api.service.XmlConversionService;
import com.dukhan.forgot.domain.model.dto.BankMiddlewareRequest;
import com.dukhan.forgot.domain.model.dto.BankMiddlewareResponse;
import com.dukhan.forgot.domain.model.dto.CardBinValidationRequest;
import com.dukhan.forgot.domain.model.dto.SimpleValidationResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@ConditionalOnProperty(name = "mock.enabled", havingValue = "false", matchIfMissing = true)
public class CardBinValidationServiceImpl implements CardBinValidationService {
    private static final Logger logger = LoggerFactory.getLogger(CardBinValidationServiceImpl.class);

    @Autowired
    private CardBinMasterRepository cardBinMasterRepository;
    @Autowired
    private HSMEncryptorManagerImpl hsmEncryptor;
    @Autowired
    private XmlConversionService xmlConversionService;
    @Autowired
    private BankMiddlewareService bankMiddlewareService;

    @Override
    public GenericResponse<SimpleValidationResponse> validateCardBin(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, CardBinValidationRequest request) {
        logger.debug("Starting CardBin validation for unit: {}, channel: {}, serviceId: {}", unit, channel, serviceId);

        try {
            String cardNumber = request.getCardNumber();
            String pin = request.getPin();
            
            CardBinMaster matchedBin = findMatchingBin(cardNumber);

            if (matchedBin == null) {
                logger.warn("Card validation failed - No CardBin record found for card number: {}", cardNumber);
                return createValidationFailureResponse();
            }

            logger.info("Card BIN validation successful - BIN: {}, ProductType: {}, CardType: {}, Code: {}",
                    matchedBin.getBin(), matchedBin.getProductType(), matchedBin.getCardType(), matchedBin.getCode());

            String encryptedPin;
            try {
                encryptedPin = hsmEncryptor.generatePinBlockUnderZPK(pin, cardNumber, "CardBinValidation");
                logger.info("PIN encryption successful for card: {}", cardNumber);
            } catch (BarwaHSMCommuicationException | BARWAHSMEncryptionException | BARWAHSMParsingException e) {
                logger.error("HSM encryption failed for card: {}, error: {}", cardNumber, e.getMessage(), e);
                return createValidationFailureResponse();
            }

            try {
                BankMiddlewareResponse bankResponse = callBankMiddlewareAPI(unit, channel, lang, serviceId, screenId, moduleId, subModuleId, cardNumber, encryptedPin);
                
                if (bankResponse != null && "SUCCESS".equals(bankResponse.getStatus())) {
                    String customerNumber = bankResponse.getBankResponse().getCustomerNumber();
                    String correlationId = bankResponse.getBankResponse().getCorrelationId();
                    
                    logger.info("Bank middleware API call successful - CustomerNumber: {}, CorrelationId: {}", customerNumber, correlationId);
                    
                    SimpleValidationResponse successResponse = createSuccessResponse(customerNumber, correlationId);
                    return GenericResponse.success(successResponse);
                } else {
                    logger.warn("Bank middleware API call failed - Status: {}, Message: {}", 
                            bankResponse != null ? bankResponse.getStatus() : "NULL", 
                            bankResponse != null ? bankResponse.getMessage() : "No response");
                    return createValidationFailureResponse();
                }
            } catch (Exception e) {
                logger.error("Bank middleware API call failed for card: {}, error: {}", cardNumber, e.getMessage(), e);
                return createValidationFailureResponse();
            }

        } catch (Exception e) {
            logger.error("Exception occurred during CardBin validation for unit: {}, channel: {}, serviceId: {}, error: {}",
                    unit, channel, serviceId, e.getMessage(), e);
            return createValidationFailureResponse();
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

    private BankMiddlewareResponse callBankMiddlewareAPI(String unit, String channel, String lang, String serviceId, 
                                                       String screenId, String moduleId, String subModuleId, 
                                                       String cardNumber, String encryptedPin) {
        try {
            BankMiddlewareRequest request = BankMiddlewareRequest.builder()
                    .serviceName("DCARD.PIN.VERIFICATION")
                    .parameters(Arrays.asList(
                            BankMiddlewareRequest.Parameter.builder()
                                    .fieldName("cardNumber")
                                    .fieldValue(cardNumber)
                                    .build(),
                            BankMiddlewareRequest.Parameter.builder()
                                    .fieldName("pin")
                                    .fieldValue(encryptedPin)
                                    .build()
                    ))
                    .build();

            logger.debug("Calling bank middleware API with cardNumber: {}", cardNumber);
            BankMiddlewareResponse response = bankMiddlewareService.callBankMiddleware(
                    unit != null ? unit : "DEFAULT",
                    channel != null ? channel : "WEB", 
                    lang != null ? lang : "en",
                    serviceId != null ? serviceId : "OTP_SERVICE",
                    screenId != null ? screenId : "LOGIN_SCREEN",
                    moduleId != null ? moduleId : "AUTH_MODULE",
                    subModuleId != null ? subModuleId : "OTP_SUBMODULE",
                    request
            );
            
            logger.debug("Bank middleware API response: {}", response);
            return response;
            
        } catch (Exception e) {
            logger.error("Error calling bank middleware API: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Create success response
     */
    private SimpleValidationResponse createSuccessResponse(String customerNumber, String correlationId) {
        return SimpleValidationResponse.builder()
                .rimNumber(customerNumber) // Using customerNumber as rimNumber
                .userName("user123")
                .otp(true)
                .build();
    }
    
    /**
     * Create validation failure error response
     */
    private GenericResponse<SimpleValidationResponse> createValidationFailureResponse() {
        return GenericResponse.error(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC);
    }

    /**
     * Generate mock XML response for testing
     */
    private String generateMockXmlResponse() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<NS1:eAI_MESSAGE xmlns:NS1=\"urn:esbbank.com/gbo/xml/schemas/v1_0/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"urn:esbbank.com/gbo/xml/schemas/v1_0/ ../testGen/schema/EAI.xsd\">\n" +
                "  <NS1:eAI_HEADER>\n" +
                "    <NS1:serviceName>DCARD.PIN.VERIFICATION</NS1:serviceName>\n" +
                "    <NS1:serviceType>SYNC</NS1:serviceType>\n" +
                "    <NS1:serviceVersion>1</NS1:serviceVersion>\n" +
                "    <NS1:client>BKR</NS1:client>\n" +
                "    <NS1:clientChannel>MOB</NS1:clientChannel>\n" +
                "    <NS1:msgChannel>MQ</NS1:msgChannel>\n" +
                "    <NS1:requestorLanguage>E</NS1:requestorLanguage>\n" +
                "    <NS1:securityInfo>\n" +
                "      <NS1:authentication>\n" +
                "        <NS1:UserId>your_user_id</NS1:UserId>\n" +
                "        <NS1:Password>your_password</NS1:Password>\n" +
                "      </NS1:authentication>\n" +
                "      <NS1:authorization>\n" +
                "        <NS1:UserId>your_user_id</NS1:UserId>\n" +
                "      </NS1:authorization>\n" +
                "    </NS1:securityInfo>\n" +
                "    <NS1:returnCode>0000</NS1:returnCode>\n" +
                "  </NS1:eAI_HEADER>\n" +
                "  <NS1:eAI_BODY>\n" +
                "    <NS1:eAI_REPLY>\n" +
                "      <NS1:debitCardPINVerificationReply>\n" +
                "        <NS1:referenceNum>TAM650</NS1:referenceNum>\n" +
                "        <NS1:requestTime>20130429233157568</NS1:requestTime>\n" +
                "        <NS1:returnStatus>\n" +
                "          <NS1:returnCode>0000</NS1:returnCode>\n" +
                "          <NS1:returnCodeDesc>Success</NS1:returnCodeDesc>\n" +
                "        </NS1:returnStatus>\n" +
                "        <NS1:returnStatusProvider>\n" +
                "          <NS1:returnCodeProvider>0000</NS1:returnCodeProvider>\n" +
                "          <NS1:returnCodeDescProvider>SUCCESS</NS1:returnCodeDescProvider>\n" +
                "        </NS1:returnStatusProvider>\n" +
                "      </NS1:debitCardPINVerificationReply>\n" +
                "    </NS1:eAI_REPLY>\n" +
                "  </NS1:eAI_BODY>\n" +
                "</NS1:eAI_MESSAGE>";
    }

    // JAXB-based unmarshal removed in favor of Jackson XmlMapper
}
