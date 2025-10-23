package com.dukhan.forgot.adapter.api.service.impl;

import com.dukhan.forgot.adapter.api.service.BankMiddlewareService;
import com.dukhan.forgot.adapter.api.service.OtpService;
import com.dukhan.forgot.domain.model.dto.*;
import com.dukhan.forgot.domain.model.entity.CardBinMaster;
import com.dukhan.forgot.domain.model.entity.CardValidation;
import com.dukhan.forgot.domain.model.entity.Customer;
import com.dukhan.forgot.domain.model.entity.OtpDetails;
import com.dukhan.forgot.domain.repository.CardBinMasterRepository;
import com.dukhan.forgot.domain.repository.CardValidationRepository;
import com.dukhan.forgot.domain.repository.CustomerRepository;
import com.dukhan.forgot.domain.repository.OtpDetailsRepository;
import com.dukhan.forgot.infrastructure.common.AppConstant;
import com.dukhan.forgot.infrastructure.common.GenericResponse;
import com.dukhan.forgot.infrastructure.common.exception.BARWAHSMEncryptionException;
import com.dukhan.forgot.infrastructure.common.hsm.HSMEncryptorManagerImpl;
import com.dukhan.forgot.infrastructure.helper.CardBasicValidations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardBinValidationServiceImplTest {

    @Mock
    private CardBinMasterRepository cardBinMasterRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private HSMEncryptorManagerImpl hsmEncryptor;

    @Mock
    private BankMiddlewareService bankMiddlewareService;

    @Mock
    private OtpService otpService;

    @Mock
    private CardBasicValidations cardBasicValidations;

    @Mock
    private DateTimeProvider dateTimeProvider;

    @Mock
    private CardValidationRepository cardValidationRepository;

    @Mock
    private OtpDetailsRepository otpDetailsRepository;

    @InjectMocks
    private CardBinValidationServiceImpl cardBinValidationService;

    private CardBinValidationRequest request;
    private CardBinMaster cardBinMaster;
    private BankMiddlewareResponse bankResponse;
    private OtpGenerateResponse otpResponse;



    @BeforeEach
    void setUp() {
        request = CardBinValidationRequest.builder()
                .cardNumber("1234567890123456")
                .pin("1234")
                .build();

        cardBinMaster = CardBinMaster.builder()
                .code("CODE1")
                .bin("123456")
                .productType("CREDIT")
                .cardType("DEBIT")
                .status("ACTIVE")
                .build();

        bankResponse = BankMiddlewareResponse.builder()
                .status("SUCCESS")
                .message("Success")
                .bankResponse(BankMiddlewareResponse.BankResponse.builder()
                        .customerNumber("123456")
                        .correlationId("CORR123")
                        .build())
                .build();

        otpResponse = OtpGenerateResponse.builder()
                .status(OtpGenerateResponse.Status.builder()
                        .code("000000")
                        .description("SUCCESS")
                        .build())
                .data(OtpGenerateResponse.OtpData.builder()
                        .mobileNumber("*******3335")
                        .message("OTP generated successfully")
                        .build())
                .build();

    }

    @Test
    void testValidateCardBin_NonDebitCardType() {
        // Given
        CardBinMaster nonDebitCardBin = CardBinMaster.builder()
                .code("CODE1")
                .bin("123456")
                .productType("CREDIT")
                .cardType("VISA") // Non-debit card type
                .status("ACTIVE")
                .build();
        when(cardBasicValidations.findMatchingBin("1234567890123456")).thenReturn(nonDebitCardBin);

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.ERROR_DATA_CODE, response.getStatus().getCode());
        assertEquals("CARD_NOT_VALID_MUST_USE_DEBIT", response.getStatus().getDescription());
    }

    @Test
    void testValidateCardBin_Success(){
        // Given
        when(cardBasicValidations.findMatchingBin("1234567890123456")).thenReturn(cardBinMaster);
        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString()))
                .thenReturn("ENCRYPTED_PIN");
        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class)))
                .thenReturn(bankResponse);
        Customer customer = Customer.builder()
                .customerId(123456L)
                .userId("testuser")
                .status("ACTIVE")
                .updatedAt(LocalDateTime.now().minusDays(2))
                .build();
        when(customerRepository.findByCustomerId(123456L)).thenReturn(Optional.of(customer));
        when(otpService.generateOtp(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(OtpGenerateRequest.class)))
                .thenReturn(otpResponse);
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now()));

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.RESULT_CODE, response.getStatus().getCode());
        assertNotNull(response.getData());
        assertEquals("123456", response.getData().getRimNumber());
        assertEquals("testuser", response.getData().getUserName());
        assertTrue(response.getData().isOtp());

        verify(cardBasicValidations, times(1)).findMatchingBin("1234567890123456");
        verify(hsmEncryptor, times(1)).generatePinBlockUnderZPK("1234", "1234567890123456", "CardBinValidation");
        verify(bankMiddlewareService, times(1)).callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class));
        verify(customerRepository, times(1)).findByCustomerId(123456L);
        verify(otpService, times(1)).generateOtp(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(OtpGenerateRequest.class));
        verify(dateTimeProvider, times(1)).getNow();
    }

    @Test
    void testValidateCardBin_UserBlocked() {
        // Given
        when(cardBasicValidations.findMatchingBin("1234567890123456")).thenReturn(cardBinMaster);
        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString()))
                .thenReturn("ENCRYPTED_PIN");
        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class)))
                .thenReturn(bankResponse);
        Customer customer = Customer.builder()
                .customerId(123456L)
                .userId("testuser")
                .status("BLOCKED") // Blocked status
                .updatedAt(LocalDateTime.now().minusDays(2))
                .build();
        when(customerRepository.findByCustomerId(123456L)).thenReturn(Optional.of(customer));
        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.ERROR_DATA_CODE, response.getStatus().getCode());
        assertEquals("USER_BLOCKED_CONTACT_BANK", response.getStatus().getDescription());
    }

    @Test
    void testValidateCardBin_InactiveBin() {
        // Given
        CardBinMaster inactiveBin = CardBinMaster.builder()
                .code("CODE1")
                .bin("123456")
                .productType("CREDIT")
                .cardType("DEBIT")
                .status("INACTIVE") // Inactive status
                .build();
        when(cardBasicValidations.findMatchingBin("1234567890123456")).thenReturn(inactiveBin);

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.ERROR_DATA_CODE, response.getStatus().getCode());
        assertEquals("BIN_NOT_VALID", response.getStatus().getDescription());

        verify(cardBasicValidations, times(1)).findMatchingBin("1234567890123456");
    }

    @Test
    void testValidateCardBin_NoBinFound() {
        // Given
        when(cardBasicValidations.findMatchingBin("1234567890123456")).thenReturn(null);

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.ERROR_DATA_CODE, response.getStatus().getCode());
        assertEquals("BIN_NOT_VALID", response.getStatus().getDescription());

        verify(cardBasicValidations, times(1)).findMatchingBin("1234567890123456");
    }

    @Test
    void testValidateCardBin_HSMEncryptionFailure() {
        // Given
        when(cardBasicValidations.findMatchingBin("1234567890123456")).thenReturn(cardBinMaster);
        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString()))
                .thenThrow(new BARWAHSMEncryptionException("HSM001", "HSM encryption failed"));

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.ERROR_DATA_CODE, response.getStatus().getCode());
        assertEquals("PIN_ENCRYPTION_FAILED", response.getStatus().getDescription());

        verify(cardBasicValidations, times(1)).findMatchingBin("1234567890123456");
        verify(hsmEncryptor, times(1)).generatePinBlockUnderZPK("1234", "1234567890123456", "CardBinValidation");
        verify(bankMiddlewareService, never()).callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class));
    }

    @Test
    void testValidateCardBin_BankMiddlewareFailure() {
        // Given
        when(cardBasicValidations.findMatchingBin("1234567890123456")).thenReturn(cardBinMaster);
        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString()))
                .thenReturn("ENCRYPTED_PIN");
        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class)))
                .thenReturn(BankMiddlewareResponse.builder().status("FAILED").message("Bank error").build());

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());

        verify(cardBasicValidations, times(1)).findMatchingBin("1234567890123456");
        verify(hsmEncryptor, times(1)).generatePinBlockUnderZPK("1234", "1234567890123456", "CardBinValidation");
        verify(bankMiddlewareService, times(1)).callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class));
    }

    @Test
    void testValidateCardBin_CustomerNotFound() {

        when(cardBasicValidations.findMatchingBin("1234567890123456")).thenReturn(cardBinMaster);
        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString()))
                .thenReturn("ENCRYPTED_PIN");

        BankMiddlewareResponse.BankResponse bankResp = BankMiddlewareResponse.BankResponse.builder()
                .customerNumber("123456")
                .correlationId("cor-123")
                .build();
        BankMiddlewareResponse bankResponse = BankMiddlewareResponse.builder()
                .status("SUCCESS")
                .bankResponse(bankResp)
                .build();

        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class)))
                .thenReturn(bankResponse);

        // Simulate customer not found
        when(customerRepository.findByCustomerId(123456L)).thenReturn(Optional.empty());

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.USER_NOT_FOUND_CODE, response.getStatus().getCode());
        assertEquals("USER_NOT_EXIST", response.getStatus().getDescription());

        verify(cardBasicValidations, times(1)).findMatchingBin("1234567890123456");
        verify(otpService, never()).generateOtp(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(OtpGenerateRequest.class));
    }

    @Test
    void testValidateCardBin_InvalidCustomerNumber() {

        when(cardBasicValidations.findMatchingBin("1234567890123456")).thenReturn(cardBinMaster);
        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString()))
                .thenReturn("ENCRYPTED_PIN");

        // Simulate bank response with customerNumber that doesn't exist in DB
        BankMiddlewareResponse.BankResponse bankResp = BankMiddlewareResponse.BankResponse.builder()
                .customerNumber("999999")
                .correlationId("corr-123")
                .build();
        BankMiddlewareResponse bankResponse = BankMiddlewareResponse.builder()
                .status("SUCCESS")
                .bankResponse(bankResp)
                .build();
        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class)))
                .thenReturn(bankResponse);

        // Simulate customer not found
        when(customerRepository.findByCustomerId(999999L)).thenReturn(Optional.empty());

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.USER_NOT_FOUND_CODE, response.getStatus().getCode());
        assertEquals("USER_NOT_EXIST", response.getStatus().getDescription());

        verify(cardBasicValidations, times(1)).findMatchingBin("1234567890123456");
        verify(customerRepository).findByCustomerId(999999L);
        verify(otpService, never()).generateOtp(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(OtpGenerateRequest.class));
    }

    @Test
    void testValidateCardBin_OTPGenerationFailure() {
        // Given

        when(cardBasicValidations.findMatchingBin("1234567890123456")).thenReturn(cardBinMaster);
        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString()))
                .thenReturn("ENCRYPTED_PIN");

        BankMiddlewareResponse.BankResponse innerBankResponse = new BankMiddlewareResponse.BankResponse();
        innerBankResponse.setCustomerNumber("123456");
        innerBankResponse.setCorrelationId("CORR_ID");

        BankMiddlewareResponse bankResponse = new BankMiddlewareResponse();
        bankResponse.setStatus("SUCCESS");
        bankResponse.setBankResponse(innerBankResponse);

        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class)))
                .thenReturn(bankResponse);

        Customer customer = Customer.builder()
                .customerId(123456L)
                .userId("testuser")
                .status("ACTIVE")
                .updatedAt(LocalDateTime.now().minusDays(2))
                .build();
        when(customerRepository.findByCustomerId(123456L)).thenReturn(Optional.of(customer));

        when(otpService.generateOtp(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(OtpGenerateRequest.class)))
                .thenReturn(OtpGenerateResponse.builder()
                        .status(OtpGenerateResponse.Status.builder()
                                .code("999999")
                                .description("FAILED")
                                .build())
                        .build());

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());

        verify(otpService, times(1)).generateOtp(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(OtpGenerateRequest.class));
    }

    @Test
    void testValidateCardBin_Exception()  {
        // Given
        when(cardBasicValidations.findMatchingBin("1234567890123456")).thenThrow(new RuntimeException("Database Error"));

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());

    }

    @Test
    void testGetActiveBins_Success() {
        // Given
        List<CardBinMaster> activeBins = Arrays.asList(cardBinMaster);
        when(cardBinMasterRepository.findAllActive()).thenReturn(activeBins);

        // When
        GenericResponse<List<CardBinMaster>> response = cardBinValidationService.getActiveBins();

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.RESULT_CODE, response.getStatus().getCode());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals("123456", response.getData().get(0).getBin());

        verify(cardBinMasterRepository, times(1)).findAllActive();
    }

    @Test
    void testGetActiveBins_Exception() {
        // Given
        when(cardBinMasterRepository.findAllActive()).thenThrow(new RuntimeException("Database error"));

        // When
        GenericResponse<List<CardBinMaster>> response = cardBinValidationService.getActiveBins();

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());

        verify(cardBinMasterRepository, times(1)).findAllActive();
    }

    @Test
    void testFindMatchingBin_With8DigitCardNumber() {
        CardBinValidationRequest request = CardBinValidationRequest.builder()
                .cardNumber("12345678")
                .pin("1234")
                .build();

        when(cardBasicValidations.findMatchingBin("12345678")).thenReturn(cardBinMaster);

        when(hsmEncryptor.generatePinBlockUnderZPK(any(), any(), any())).thenReturn("ENCRYPTED_PIN");

        BankMiddlewareResponse.BankResponse innerResponse = new BankMiddlewareResponse.BankResponse();
        innerResponse.setCustomerNumber("123456");
        innerResponse.setCorrelationId("ABC123");

        BankMiddlewareResponse bankResponse = new BankMiddlewareResponse();
        bankResponse.setStatus("SUCCESS");
        bankResponse.setBankResponse(innerResponse);

        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class)))
                .thenReturn(bankResponse);

        Customer mockCustomer = new Customer();
        mockCustomer.setStatus("ACTIVE");
        mockCustomer.setUserId("testuser");
        mockCustomer.setUpdatedAt(LocalDateTime.now().minusDays(2));

        when(customerRepository.findByCustomerId(123456L)).thenReturn(Optional.of(mockCustomer));

        OtpGenerateResponse otpGenerateResponse = OtpGenerateResponse.builder()
                .status(OtpGenerateResponse.Status.builder()
                        .code(AppConstant.RESULT_CODE)
                        .description(AppConstant.SUCCESS)
                        .build())
                .build();

        when(otpService.generateOtp(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(otpGenerateResponse);

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.RESULT_CODE, response.getStatus().getCode());

        verify(cardBasicValidations, times(1)).findMatchingBin("12345678");
    }

    @Test
    void testFindMatchingBin_NullCardNumber() {

        when(cardBasicValidations.findMatchingBin(anyString())).thenReturn(null);

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.ERROR_DATA_CODE, response.getStatus().getCode());
        assertEquals("BIN_NOT_VALID", response.getStatus().getDescription());
    }

    @Test
    void testInvalidCustomerNumberFormat() throws Exception {
        when(cardValidationRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());

        when(cardBasicValidations.findMatchingBin(anyString())).thenReturn(cardBinMaster);

        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString())).thenReturn("encryptedPin");

        bankResponse.getBankResponse().setCustomerNumber("invalid");

        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any()))
                .thenReturn(bankResponse);

        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "UNIT", "WEB", "en", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);


        assertEquals(AppConstant.USER_NOT_FOUND_CODE, response.getStatus().getCode());
        assertEquals("USER_NOT_EXIST", response.getStatus().getDescription());
    }

    @Test
    void testValidateCardBin_CardBlocked() throws Exception {

        CardValidation cardValidation = CardValidation.builder()
                .id(1L)
                .cardNumber("1234567890123456")
                .attempts(4)
                .build();

        when(cardValidationRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.of(cardValidation));

        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "UNIT", "WEB", "en", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);


        assertEquals(AppConstant.INVALID_ATTAMPTS_CODE, response.getStatus().getCode());
        assertEquals("INVALID_ATTEMPTS_LIMIT_EXCEEDED", response.getStatus().getDescription());

        verify(cardValidationRepository, times(1)).findByCardNumber("1234567890123456");
    }

    @Test
    void testValidateCardBin_RetryAfter24Hours() throws Exception {

        when(cardValidationRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());

        when(cardBasicValidations.findMatchingBin(anyString())).thenReturn(cardBinMaster);

        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString())).thenReturn("encryptedPin");

        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any()))
                .thenReturn(bankResponse);

        Customer customer = new Customer();
        customer.setStatus("ACTIVE");

        customer.setUpdatedAt(LocalDateTime.now().minusHours(1));

        when(customerRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(customer));

        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now()));

        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "UNIT", "WEB", "en", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        assertEquals(AppConstant.RETRY_DATA_CODE, response.getStatus().getCode());
        assertEquals("RETRY_AFTER_24_HOURS", response.getStatus().getDescription());
    }

    @Test
    void testValidateCardBin_OtpBlocked() throws Exception {

        when(cardValidationRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());

        when(cardBasicValidations.findMatchingBin(anyString())).thenReturn(cardBinMaster);

        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString())).thenReturn("encryptedPin");

        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any()))
                .thenReturn(bankResponse);

        Customer customer = new Customer();
        customer.setStatus("ACTIVE");
        customer.setUserId("testuser");

        customer.setUpdatedAt(LocalDateTime.now().minusDays(2));

        when(customerRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(customer));

        when(otpDetailsRepository.findBlockedOtpByUserId(anyLong(), anyInt()))
                .thenReturn(List.of(new OtpDetails()));

        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "UNIT", "WEB", "en", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        assertEquals(AppConstant.OTP_LIMIT, response.getStatus().getCode());
        assertEquals("USER_BLOCKED_OTP_LIMIT_EXCEEDED", response.getStatus().getDescription());
    }

    @Test
    void testValidationCardBin_incrementOtpAttempts() {

        when(cardValidationRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());

        when(cardBasicValidations.findMatchingBin(anyString())).thenReturn(cardBinMaster);

        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString())).thenReturn("encryptedPin");

        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any()))
                .thenReturn(bankResponse);

        Customer customer = Customer.builder()
                .customerId(123456L)
                .userId("testuser")
                .status("ACTIVE")
                .updatedAt(LocalDateTime.now().minusDays(2))
                .build();
        when(customerRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(customer));

        OtpDetails otpDetails = OtpDetails.builder()
                .id(10L)
                .email("test@gmail.com")
                .rimNo(222L)
                .noOfAttempts(1)
                .language("Arabic")
                .build();

        when(otpDetailsRepository.findActiveOtpByUserId(Long.valueOf("123456"))).thenReturn(List.of(otpDetails));

        OtpGenerateResponse otpGenerateResponse = OtpGenerateResponse.builder()
                .status(OtpGenerateResponse.Status.builder()
                        .code(AppConstant.RESULT_CODE)
                        .description(AppConstant.SUCCESS)
                        .build())
                .build();

        when(otpService.generateOtp(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(otpGenerateResponse);

        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "UNIT", "WEB", "en", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        assertEquals(AppConstant.RESULT_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.SUCCESS, response.getStatus().getDescription());

        verify(cardBasicValidations, times(1)).findMatchingBin(anyString());

        verify(cardBasicValidations, times(1)).findMatchingBin("1234567890123456");

        verify(customerRepository).findByCustomerId(anyLong());

        verify(otpService, times(1)).generateOtp(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(OtpGenerateRequest.class));

    }

    @Test
    void testValidationCardBin_incrementOtpAttemptsFailure() {

        when(cardValidationRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());

        when(cardBasicValidations.findMatchingBin(anyString())).thenReturn(cardBinMaster);

        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString())).thenReturn("encryptedPin");

        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any()))
                .thenReturn(bankResponse);

        Customer customer = Customer.builder()
                .customerId(123456L)
                .userId("testuser")
                .status("ACTIVE")
                .updatedAt(LocalDateTime.now().minusDays(2))
                .build();
        when(customerRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(customer));

        when(otpDetailsRepository.findActiveOtpByUserId(Long.valueOf("123456"))).thenThrow(new RuntimeException("Database Error"));

        OtpGenerateResponse otpGenerateResponse = OtpGenerateResponse.builder()
                .status(OtpGenerateResponse.Status.builder()
                        .code(AppConstant.RESULT_CODE)
                        .description(AppConstant.SUCCESS)
                        .build())
                .build();

        when(otpService.generateOtp(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(otpGenerateResponse);

        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "UNIT", "WEB", "en", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        assertEquals(AppConstant.RESULT_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.SUCCESS, response.getStatus().getDescription());

        verify(cardBasicValidations, times(1)).findMatchingBin(anyString());

        verify(cardBasicValidations, times(1)).findMatchingBin("1234567890123456");

        verify(customerRepository).findByCustomerId(anyLong());

        verify(otpService, times(1)).generateOtp(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(OtpGenerateRequest.class));

    }
}