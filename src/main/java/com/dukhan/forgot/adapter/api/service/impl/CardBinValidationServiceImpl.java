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
    public GenericResponse<SimpleValidationResponse> validateCardBin(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, CardBinValidationRequest request) {
        logger.debug("Starting CardBin validation for unit: {}, channel: {}, serviceId: {}", unit, channel, serviceId);

        try {
            String cardNumber = request.getCardNumber();
            String pin = request.getPin();

            if (isCardBlocked(cardNumber)) {
                logger.warn("Card is blocked due to maximum failed attempts - CardNumber: {}", cardNumber);
                return GenericResponse.error(AppConstant.INVALID_ATTAMPTS_CODE, "INVALID_ATTEMPTS_LIMIT_EXCEEDED");
            }

            CardBinMaster matchedBin = cardBasicValidations.findMatchingBin(cardNumber);

            if (matchedBin == null) {
                logger.warn("Card validation failed - No CardBin record found for card number: {}", cardNumber);
                handleFailedAttempt(cardNumber);
                return GenericResponse.error(AppConstant.ERROR_DATA_CODE, "BIN_NOT_VALID");
            }

            if (!"ACTIVE".equalsIgnoreCase(matchedBin.getStatus())) {
                logger.warn("Card validation failed - BIN record is not ACTIVE. BIN: {}, Status: {}", matchedBin.getBin(), matchedBin.getStatus());
                handleFailedAttempt(cardNumber);
                return GenericResponse.error(AppConstant.ERROR_DATA_CODE, "BIN_NOT_VALID");
            }

            if (matchedBin.getCardType() != null && !"DEBIT".equalsIgnoreCase(matchedBin.getCardType())) {
                logger.warn("Card validation failed - Card type must be DEBIT. BIN: {}, CardType: {}", matchedBin.getBin(), matchedBin.getCardType());
                handleFailedAttempt(cardNumber);
                return GenericResponse.error(AppConstant.ERROR_DATA_CODE, "CARD_NOT_VALID_MUST_USE_DEBIT");
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
                return GenericResponse.error(AppConstant.ERROR_DATA_CODE, "PIN_ENCRYPTION_FAILED");
            }

            try {
                BankMiddlewareResponse bankResponse = callBankMiddlewareAPI(unit, channel, lang, serviceId, screenId, moduleId, subModuleId, cardNumber, encryptedPin);
                if (bankResponse != null && "SUCCESS".equals(bankResponse.getStatus())) {
                    String customerNumber = bankResponse.getBankResponse().getCustomerNumber();
                    String correlationId = bankResponse.getBankResponse().getCorrelationId();

                    logger.info("Bank middleware API call successful - CustomerNumber: {}, CorrelationId: {}", customerNumber, correlationId);

                    String username;
                    try {
                        username = getCustomerUsername(customerNumber);
                    } catch (UserBlockedException ex) {
                        logger.warn("User is blocked for customerNumber: {}", customerNumber);
                        return GenericResponse.error(AppConstant.ERROR_DATA_CODE, "USER_BLOCKED_CONTACT_BANK");
                    } catch (RetryAfter24HoursException ex) {
                        logger.warn("User must retry after 24 hours for customerNumber: {}", customerNumber);
                        return GenericResponse.error(AppConstant.RETRY_DATA_CODE, "RETRY_AFTER_24_HOURS");
                    }
                    if (username == null) {
                        logger.warn("Customer not found in database for customerNumber: {}", customerNumber);
                        return GenericResponse.error(AppConstant.USER_NOT_FOUND_CODE, "USER_NOT_EXIST");
                    }

                    if (isOtpBlocked(customerNumber)) {
                        logger.warn("User is blocked due to OTP limit exceeded - Username: {}", username);
                        return GenericResponse.error(AppConstant.OTP_LIMIT, "USER_BLOCKED_OTP_LIMIT_EXCEEDED");
                    }

                    OtpGenerateResponse otpResponse = callOtpGenerationAPI(unit, channel, lang, serviceId, screenId, moduleId, subModuleId, customerNumber);
                    if (otpResponse != null && otpResponse.getStatus() != null &&
                        AppConstant.RESULT_CODE.equals(otpResponse.getStatus().getCode()) &&
                      AppConstant.SUCCESS.equals(otpResponse.getStatus().getDescription())) {
                        logger.info("OTP generation successful for customer: {}", customerNumber);
                        resetFailedAttempts(cardNumber);
                        incrementOtpAttempts(customerNumber);
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
                    handleFailedAttempt(cardNumber);
                    return createValidationFailureResponse();
                }
            } catch (Exception e) {
                logger.error("Bank middleware API call failed for card: {}, error: {}", cardNumber, e.getMessage(), e);
                handleFailedAttempt(cardNumber);
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
            return customerRepository.findByCustomerId(customerId)
                    .map(customer -> {
                        String status = customer.getStatus();
                        if (status != null && (
                                "LOCKED".equalsIgnoreCase(status) ||
                                "BLOCKED".equalsIgnoreCase(status) ||
                                "INACTIVE".equalsIgnoreCase(status))) {
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

    private boolean isOtpBlocked(String username) {
        try {
            List<OtpDetails> blockedOtps = otpDetailsRepository.findBlockedOtpByUserId(Long.valueOf(username), OtpDetails.MAX_OTP_ATTEMPTS);
            return !blockedOtps.isEmpty();
        } catch (Exception e) {
            logger.error("Error checking OTP attempts for username: {}, error: {}", username, e.getMessage(), e);
            return false;
        }
    }

    private boolean handleFailedAttempt(String cardNumber) {
        try {
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

    /**
     * Increments OTP attempts for successful OTP generation
     * @param username The username
     */
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
                                                   String customerNumber) {
        try {
            OtpGenerateRequest otpRequest = OtpGenerateRequest.builder()
                    .requestInfo(OtpGenerateRequest.RequestInfo.builder()
                            .action("forget")
                            .rimNo(customerNumber)
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
