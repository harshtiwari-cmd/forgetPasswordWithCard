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
import com.dukhan.forgot.domain.model.entity.CardValidation;
import com.dukhan.forgot.domain.model.entity.OtpDetails;
import com.dukhan.forgot.domain.repository.CardBinMasterRepository;
import com.dukhan.forgot.domain.repository.CustomerRepository;
import com.dukhan.forgot.domain.repository.CardValidationRepository;
import com.dukhan.forgot.domain.repository.OtpDetailsRepository;
import com.dukhan.forgot.infrastructure.common.AppConstant;
import com.dukhan.forgot.infrastructure.common.GenericResponse;
import com.dukhan.forgot.infrastructure.common.exception.BARWAHSMEncryptionException;
import com.dukhan.forgot.infrastructure.common.exception.BARWAHSMParsingException;
import com.dukhan.forgot.infrastructure.common.exception.BarwaHSMCommuicationException;
import com.dukhan.forgot.infrastructure.common.hsm.HSMEncryptorManagerImpl;
import com.dukhan.forgot.infrastructure.common.exception.UserBlockedException;
import com.dukhan.forgot.infrastructure.common.exception.RetryAfter24HoursException;
import com.dukhan.forgot.infrastructure.helper.CardBasicValidations;
import org.springframework.data.auditing.DateTimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    @Autowired
    private CardBasicValidations cardBasicValidations;
    @Autowired
    private DateTimeProvider dateTimeProvider;
    @Autowired
    private CardValidationRepository cardValidationRepository;
    @Autowired
    private OtpDetailsRepository otpDetailsRepository;

    @Override
    public GenericResponse<SimpleValidationResponse> validateCardBin(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, CardBinValidationRequest request, DeviceInfo deviceInfo) {
        logger.debug("Starting CardBin validation for unit: {}, channel: {}, serviceId: {}", unit, channel, serviceId);

        try {
            String cardNumber = request.getCardNumber();
            String pin = request.getPin();

            if (isCardBlocked(cardNumber)) {
                logger.warn("Card is blocked due to maximum failed attempts - CardNumber: {}", cardNumber);
                return GenericResponse.error(AppConstant.INVALID_ATTAMPTS_CODE, AppConstant.INVALID_ATTAMPTS_MSG);
            }

            CardBinMaster matchedBin = cardBasicValidations.findMatchingBin(cardNumber);

            if (matchedBin == null) {
                logger.warn("Card validation failed - No CardBin record found for card number: {}", cardNumber);
                handleFailedAttempt(cardNumber);
                return GenericResponse.error(AppConstant.ERROR_DATA_CODE, AppConstant.BIN_VALIDATE_DATA_MSG);
            }

            if (!"ACTIVE".equalsIgnoreCase(matchedBin.getStatus())) {
                logger.warn("Card validation failed - BIN record is not ACTIVE. BIN: {}, Status: {}", matchedBin.getBin(), matchedBin.getStatus());
                handleFailedAttempt(cardNumber);
                return GenericResponse.error(AppConstant.ERROR_DATA_CODE, AppConstant.BIN_VALIDATE_DATA_MSG);
            }

            if (matchedBin.getCardType() != null && !"DEBIT".equalsIgnoreCase(matchedBin.getCardType()) && !"PREPAID".equalsIgnoreCase(matchedBin.getCardType())) {
                logger.warn("Card validation failed - Card type must be DEBIT. BIN: {}, CardType: {}", matchedBin.getBin(), matchedBin.getCardType());
                handleFailedAttempt(cardNumber);
                return GenericResponse.error(AppConstant.ERROR_DATA_CODE, AppConstant.BIN_VALIDATE_DATA_MSG);
            }

            logger.info("Card BIN validation successful - BIN: {}, ProductType: {}, CardType: {}, Code: {}",
                    matchedBin.getBin(), matchedBin.getProductType(), matchedBin.getCardType(), matchedBin.getCode());

            String encryptedPin;
            try {
                encryptedPin = hsmEncryptor.generatePinBlockUnderZPK(pin, cardNumber, "CardBinValidation");
                logger.info("PIN encryption successful for card: {}", cardNumber);
            } catch (BarwaHSMCommuicationException | BARWAHSMEncryptionException | BARWAHSMParsingException e) {
                logger.error("HSM encryption failed for card: {}, error: {}", cardNumber, e.getMessage(), e);
                handleFailedAttempt(cardNumber);
                return GenericResponse.error(AppConstant.ERROR_DATA_CODE, AppConstant.PIN_ENCRYPT_DATA_MSG);
            }

            try {
                BankMiddlewareResponse bankResponse = callBankMiddlewareAPI(unit, channel, lang, serviceId, screenId, moduleId, subModuleId, cardNumber, encryptedPin, deviceInfo);
                if (bankResponse != null && "SUCCESS".equals(bankResponse.getStatus())) {
                    String customerNumber = bankResponse.getBankResponse().getCustomerNumber();
                    String correlationId = bankResponse.getBankResponse().getCorrelationId();
                    if(customerNumber == null){
                        if(AppConstant.INVALID_PIN_BLOCK.equals(bankResponse.getBankResponse().getReturnStatusProvider().getReturnCodeDescProvider())) {
                            return GenericResponse.error(AppConstant.USER_NOT_FOUND_CODE, AppConstant.INVALID_PIN_BLOCK);
                        }
                        return GenericResponse.error(AppConstant.USER_NOT_FOUND_CODE, AppConstant.USER_NOT_FOUND_MSG);
                    }

                    logger.info("Bank middleware API call successful - CustomerNumber: {}, CorrelationId: {}", customerNumber, correlationId);

                    String username;
                    try {
                        username = getCustomerUsername(customerNumber);
                    } catch (UserBlockedException ex) {
                        logger.warn("User is blocked for customerNumber: {}", customerNumber);
                        return GenericResponse.error(AppConstant.USER_BLOCKED, AppConstant.USER_BLOCKED_DATA_MSG);
                    } catch (RetryAfter24HoursException ex) {
                        logger.warn("User must retry after 24 hours for customerNumber: {}", customerNumber);
                        return GenericResponse.error(AppConstant.RETRY_DATA_CODE, AppConstant.RETRY_DATA_MSG);
                    }
                    if (username == null) {
                        logger.warn("Customer not found in database for customerNumber: {}", customerNumber);
                        return GenericResponse.error(AppConstant.USER_NOT_FOUND_CODE, AppConstant.USER_NOT_FOUND_MSG);
                    }

                    if (isOtpBlocked(customerNumber)) {
                        logger.warn("User is blocked due to OTP limit exceeded - Username: {}", username);
                        return GenericResponse.error(AppConstant.OTP_LIMIT, AppConstant.OTP_LIMIT_MSG);
                    }

                    OtpGenerateResponse otpResponse = callOtpGenerationAPI(unit, channel, lang, serviceId, screenId, moduleId, subModuleId, customerNumber, deviceInfo);
                    if (otpResponse != null && otpResponse.getStatus() != null &&
                            AppConstant.RESULT_CODE.equals(otpResponse.getStatus().getCode()) &&
                            AppConstant.SUCCESS.equals(otpResponse.getStatus().getDescription())) {
                        String jwtToken=otpResponse.getData().getJwtToken();
                        logger.info("OTP generation successful for customer: {}", customerNumber);
                        resetFailedAttempts(cardNumber);
                        incrementOtpAttempts(customerNumber);
                        SimpleValidationResponse successResponse = createSuccessResponseWithUsername(customerNumber, username, jwtToken);
                        return GenericResponse.success(successResponse);
                    } else {
                        logger.warn("OTP generation failed - Status: {}, Message: {}",
                                otpResponse != null && otpResponse.getStatus() != null ?
                                        otpResponse.getStatus().getDescription() : "NULL",
                                otpResponse != null && otpResponse.getData() != null ?
                                        otpResponse.getData().getMessage() : "No response");
                        return GenericResponse.error(AppConstant.OTP_GENERATE, AppConstant.OTP_GENERATE_MSG);
                    }
                } else {
                    logger.warn("Bank middleware API call failed - Status: {}, Message: {}",
                            bankResponse != null ? bankResponse.getStatus() : "NULL",
                            bankResponse != null ? bankResponse.getMessage() : "No response");
                    handleFailedAttempt(cardNumber);
                    return GenericResponse.error(AppConstant.INNER_SERVICE, AppConstant.INNER_SERVICE_MSG);
                }
            } catch (Exception e) {
                logger.error("Bank middleware API call failed for card: {}, error: {}", cardNumber, e.getMessage(), e);
                handleFailedAttempt(cardNumber);
                return GenericResponse.error(AppConstant.INNER_SERVICE, AppConstant.INNER_SERVICE_MSG);
            }

        } catch (Exception e) {
            logger.error("Exception occurred during CardBin validation for unit: {}, channel: {}, serviceId: {}, error: {}",
                    unit, channel, serviceId, e.getMessage(), e);
            return createValidationFailureResponse();
        }
    }

    private boolean isOtpBlocked(String username) {
        try {
            List<OtpDetails> blockedOtps = otpDetailsRepository.findBlockedOtpByUserId(Long.valueOf(username), OtpDetails.MAX_OTP_ATTEMPTS);
            return !blockedOtps.isEmpty();
        } catch (Exception e) {
            logger.error("Error checking OTP attempts for username: {}, error: {}", username, e.getMessage(), e);
            return false;
        }
    }
    @Override
    public GenericResponse<List<CardBinMaster>> getActiveBins() {
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


    private BankMiddlewareResponse callBankMiddlewareAPI(String unit, String channel, String lang, String serviceId,
                                                         String screenId, String moduleId, String subModuleId,
                                                         String cardNumber, String encryptedPin, DeviceInfo deviceInfo) {
        try {
            BankMiddlewareRequest request = BankMiddlewareRequest.builder()
                    .serviceName(AppConstant.DCARD_SERVICE)
                    .parameters(Arrays.asList(
                            BankMiddlewareRequest.Parameter.builder()
                                    .fieldName(AppConstant.CARD_NUMBER)
                                    .fieldValue(cardNumber)
                                    .build(),
                            BankMiddlewareRequest.Parameter.builder()
                                    .fieldName(AppConstant.PIN)
                                    .fieldValue(encryptedPin)
                                    .build()
                    ))
                    .build();

            logger.debug("Calling bank middleware API with cardNumber: {}", cardNumber);
            BankMiddlewareResponse response = bankMiddlewareService.callBankMiddleware(
                    unit != null ? unit : AppConstant.DEFAULT_UNIT,
                    channel != null ? channel : AppConstant.DEFAULT_CHANNEL,
                    lang != null ? lang :AppConstant.DEFAULT_LANGUAGE,
                    serviceId != null ? serviceId : AppConstant.DEFAULT_SERVICEID,
                    screenId != null ? screenId : AppConstant.DEFAULT_SCREENID,
                    moduleId != null ? moduleId : AppConstant.DEFAULT_MODULEID,
                    subModuleId != null ? subModuleId : AppConstant.DEFAULT_SUNMODULEID,
                    request
            );

            logger.debug("Bank middleware API response: {}", response);
            return response;

        } catch (Exception e) {
            logger.error("Error calling bank middleware API: {}", e.getMessage(), e);
            throw e;
        }
    }


    private SimpleValidationResponse createSuccessResponseWithUsername(String customerNumber, String username,String jwtToken) {
        return SimpleValidationResponse.builder()
                .customerId(customerNumber)
                .userName(username)
                .otpStatus(true)
                .jwtToken(jwtToken)
                .build();
    }

    private String getCustomerUsername(String customerNumber) {
        try {
            logger.debug("Looking up customer username for customerNumber: {}", customerNumber);
            Long customerId = Long.parseLong(customerNumber);
            return customerRepository.findByCustomerId(customerId)
                    .map(customer -> {
                        String status = customer.getStatus();
                        if (status != null && (
                                AppConstant.LOCKED.equalsIgnoreCase(status) ||
                                        AppConstant.BLOCKED.equalsIgnoreCase(status) ||
                                        AppConstant.INVALID.equalsIgnoreCase(status))) {
                            throw new UserBlockedException("User is blocked");
                        }
                        if (customer.getUpdatedAt() != null) {
                            LocalDateTime updatedAt = customer.getUpdatedAt();
                            LocalDateTime now = dateTimeProvider.getNow()
                                    .map(temporal -> {
                                        try {
                                            return LocalDateTime.from(temporal);
                                        } catch (Exception ex) {
                                            return LocalDateTime.ofInstant(java.time.Instant.from(temporal), java.time.ZoneId.systemDefault());
                                        }
                                    })
                                    .orElseGet(LocalDateTime::now);
                            if (updatedAt.isAfter(now.minusHours(24))) {
                                throw new RetryAfter24HoursException("Retry after 24 hours");
                            }
                        }
                        return customer.getUserId();
                    }).orElse(null);
        } catch (NumberFormatException e) {
            logger.error("Invalid customerNumber format: {}, must be a valid number", customerNumber);
            return null;
        } catch (Exception e) {
            logger.error("Error retrieving customer username for customerNumber: {}, error: {}", customerNumber, e.getMessage(), e);
            throw e;
        }
    }
    private boolean handleFailedAttempt(String cardNumber) {
        try {
            logger.info("CardNumber: ",cardNumber);
            CardValidation cardValidation = cardValidationRepository.findByCardNumber(cardNumber)
                    .orElse(CardValidation.builder()
                            .cardNumber(cardNumber)
                            .attempts(0)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build());

            boolean isBlocked = cardValidation.incrementAttempts();
            cardValidationRepository.save(cardValidation);

            if (isBlocked) {
                logger.warn("Card blocked due to maximum failed attempts - CardNumber: {}, Attempts: {}",
                        cardNumber, cardValidation.getAttempts());
            } else {
                logger.warn("Failed attempt recorded - CardNumber: {}, Attempts: {}/{}",
                        cardNumber, cardValidation.getAttempts(), CardValidation.MAX_FAILED_ATTEMPTS);
            }

            return isBlocked;
        } catch (Exception e) {
            logger.error("Error handling failed attempt for cardNumber: {}, error: {}", cardNumber, e.getMessage(), e);
            return false;
        }
    }

    private void resetFailedAttempts(String cardNumber) {
        try {
            cardValidationRepository.findByCardNumber(cardNumber)
                    .ifPresent(cardValidation -> {
                        cardValidation.resetAttempts();
                        cardValidationRepository.save(cardValidation);
                        logger.info("Failed attempts reset for successful validation - CardNumber: {}", cardNumber);
                    });
        } catch (Exception e) {
            logger.error("Error resetting failed attempts for cardNumber: {}, error: {}", cardNumber, e.getMessage(), e);
        }
    }
    private boolean isCardBlocked(String cardNumber) {
        try {
            return cardValidationRepository.findByCardNumber(cardNumber)
                    .map(CardValidation::isBlocked)
                    .orElse(false);
        } catch (Exception e) {
            logger.error("Error checking if card is blocked for cardNumber: {}, error: {}", cardNumber, e.getMessage(), e);
            return false;
        }
    }

    private void incrementOtpAttempts(String username) {
        try {
            List<OtpDetails> activeOtps = otpDetailsRepository.findActiveOtpByUserId(Long.valueOf(username));
            for (OtpDetails otp : activeOtps) {
                otp.incrementOtpAttempts();
                otpDetailsRepository.save(otp);
                logger.info("OTP attempts incremented for username: {}, attempts: {}", username, otp.getNoOfAttempts());
            }
        } catch (Exception e) {
            logger.error("Error incrementing OTP attempts for username: {}, error: {}", username, e.getMessage(), e);
        }
    }

    private OtpGenerateResponse callOtpGenerationAPI(String unit, String channel, String lang, String serviceId,
                                                     String screenId, String moduleId, String subModuleId,
                                                     String customerNumber, DeviceInfo deviceInfo) {
        try {
            OtpGenerateRequest otpRequest = OtpGenerateRequest.builder()
                    .requestInfo(OtpGenerateRequest.RequestInfo.builder()
                            .action(AppConstant.OTP_FORGET)
                            .customerId(customerNumber)
                            .build())
                    .deviceInfo(DeviceInfo.builder()
                            .deviceId(deviceInfo.getDeviceId())
                            .ipAddress(deviceInfo.getIpAddress())
                            .vendorId(deviceInfo.getVendorId())
                            .osVersion(deviceInfo.getOsVersion())
                            .osType(deviceInfo.getOsType())
                            .appVersion(deviceInfo.getAppVersion())
                            .endToEndId(deviceInfo.getEndToEndId())
                            .build())
                    .build();

            logger.debug("Calling OTP generation API for customerNumber: {}", customerNumber);
            OtpGenerateResponse response = otpService.generateOtp(
                    unit != null ? unit : AppConstant.DEFAULT_UNIT,
                    channel != null ? channel : AppConstant.DEFAULT_CHANNEL,
                    lang != null ? lang :AppConstant.DEFAULT_LANGUAGE,
                    serviceId != null ? serviceId : AppConstant.DEFAULT_SERVICEID,
                    screenId != null ? screenId : AppConstant.DEFAULT_SCREENID,
                    moduleId != null ? moduleId : AppConstant.DEFAULT_MODULEID,
                    subModuleId != null ? subModuleId : AppConstant.DEFAULT_SUNMODULEID,
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
