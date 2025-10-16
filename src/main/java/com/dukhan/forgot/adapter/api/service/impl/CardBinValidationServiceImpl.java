package com.dukhan.forgot.adapter.api.service.impl;

import com.dukhan.forgot.adapter.api.service.BankMiddlewareService;
import com.dukhan.forgot.adapter.api.service.CardBinValidationService;
import com.dukhan.forgot.adapter.api.service.OtpService;
import com.dukhan.forgot.domain.model.dto.BankMiddlewareRequest;
import com.dukhan.forgot.domain.model.dto.BankMiddlewareResponse;
import com.dukhan.forgot.domain.model.dto.CardBinValidationRequest;
import com.dukhan.forgot.domain.model.dto.DeviceInfo;
import com.dukhan.forgot.domain.model.dto.OtpGenerateRequest;
import com.dukhan.forgot.domain.model.dto.OtpGenerateResponse;
import com.dukhan.forgot.domain.model.dto.SimpleValidationResponse;
import com.dukhan.forgot.domain.model.entity.CardBinMaster;
import com.dukhan.forgot.domain.repository.CardBinMasterRepository;
import com.dukhan.forgot.domain.repository.CustomerRepository;
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
    private CustomerRepository customerRepository;
    @Autowired
    private HSMEncryptorManagerImpl hsmEncryptor;
    @Autowired
    private BankMiddlewareService bankMiddlewareService;
    @Autowired
    private OtpService otpService;

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
                    
                    String username = getCustomerUsername(customerNumber);
                    if (username == null) {
                        logger.warn("Customer not found in database for customerNumber: {}", customerNumber);
                        return createValidationFailureResponse();
                    }
                    OtpGenerateResponse otpResponse = callOtpGenerationAPI(unit, channel, lang, serviceId, screenId, moduleId, subModuleId, customerNumber);
                    if (otpResponse != null && otpResponse.getStatus() != null &&
                        "000000".equals(otpResponse.getStatus().getCode()) && 
                        "SUCCESS".equals(otpResponse.getStatus().getDescription())) {
                        logger.info("OTP generation successful for customer: {}", customerNumber);
                        
                        SimpleValidationResponse successResponse = createSuccessResponseWithUsername(customerNumber, username);
                        return GenericResponse.success(successResponse);
                    } else {
                        logger.warn("OTP generation failed - Status: {}, Message: {}", 
                                otpResponse != null && otpResponse.getStatus() != null ? 
                                    otpResponse.getStatus().getDescription() : "NULL", 
                                otpResponse != null && otpResponse.getData() != null ? 
                                    otpResponse.getData().getMessage() : "No response");
                        return createValidationFailureResponse();
                    }
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
    

    private SimpleValidationResponse createSuccessResponseWithUsername(String customerNumber, String username) {
        return SimpleValidationResponse.builder()
                .rimNumber(customerNumber)
                .userName(username)
                .otp(true)
                .build();
    }

    private String getCustomerUsername(String customerNumber) {
        try {
            logger.debug("Looking up customer username for customerNumber: {}", customerNumber);
            Long customerId = Long.parseLong(customerNumber);
            return customerRepository.findUsernameByCustomerId(customerId).orElse(null);
        } catch (NumberFormatException e) {
            logger.error("Invalid customerNumber format: {}, must be a valid number", customerNumber);
            return null;
        } catch (Exception e) {
            logger.error("Error retrieving customer username for customerNumber: {}, error: {}", customerNumber, e.getMessage(), e);
            return null;
        }
    }

    private OtpGenerateResponse callOtpGenerationAPI(String unit, String channel, String lang, String serviceId, 
                                                   String screenId, String moduleId, String subModuleId, 
                                                   String customerNumber) {
        try {
            OtpGenerateRequest otpRequest = OtpGenerateRequest.builder()
                    .requestInfo(OtpGenerateRequest.RequestInfo.builder()
                            .action("login")
                            .rimNumber(customerNumber)
                            .build())
                    .deviceInfo(DeviceInfo.builder()
                            .deviceId("DEVICE123")
                            .ipAddress("192.168.1.1")
                            .vendorId("VENDOR123")
                            .osVersion("1.0.0")
                            .osType("Android")
                            .appVersion("2.1.0")
                            .endToEndId("E2E123")
                            .build())
                    .build();

            logger.debug("Calling OTP generation API for customerNumber: {}", customerNumber);
            OtpGenerateResponse response = otpService.generateOtp(
                    unit != null ? unit : "DEFAULT",
                    channel != null ? channel : "WEB", 
                    lang != null ? lang : "en",
                    serviceId != null ? serviceId : "OTP_SERVICE",
                    screenId != null ? screenId : "LOGIN_SCREEN",
                    moduleId != null ? moduleId : "AUTH_MODULE",
                    subModuleId != null ? subModuleId : "OTP_SUBMODULE",
                    otpRequest
            );
            
            logger.debug("OTP generation API response: {}", response);
            return response;
            
        } catch (Exception e) {
            logger.error("Error calling OTP generation API: {}", e.getMessage(), e);
            throw e;
        }
    }

    private GenericResponse<SimpleValidationResponse> createValidationFailureResponse() {
        return GenericResponse.error(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC);
    }

}
