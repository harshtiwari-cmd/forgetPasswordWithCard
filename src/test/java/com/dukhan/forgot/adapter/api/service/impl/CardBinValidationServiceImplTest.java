package com.dukhan.forgot.adapter.api.service.impl;

import com.dukhan.forgot.adapter.api.service.BankMiddlewareService;
import com.dukhan.forgot.adapter.api.service.OtpService;
import com.dukhan.forgot.domain.model.dto.*;
import com.dukhan.forgot.domain.model.entity.CardBinMaster;
import com.dukhan.forgot.domain.repository.CardBinMasterRepository;
import com.dukhan.forgot.domain.repository.CustomerRepository;
import com.dukhan.forgot.infrastructure.common.AppConstant;
import com.dukhan.forgot.infrastructure.common.GenericResponse;
import com.dukhan.forgot.infrastructure.common.exception.BARWAHSMEncryptionException;
import com.dukhan.forgot.infrastructure.common.hsm.HSMEncryptorManagerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
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
                .cardType("VISA")
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
    void testValidateCardBin_Success() throws Exception {
        // Given
        when(cardBinMasterRepository.findByBin("12345678")).thenReturn(Arrays.asList(cardBinMaster));
        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString()))
                .thenReturn("ENCRYPTED_PIN");
        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class)))
                .thenReturn(bankResponse);
        when(customerRepository.findUsernameByCustomerId(123456L)).thenReturn(Optional.of("testuser"));
        when(otpService.generateOtp(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(OtpGenerateRequest.class)))
                .thenReturn(otpResponse);

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

        verify(cardBinMasterRepository, times(1)).findByBin("12345678");
        verify(hsmEncryptor, times(1)).generatePinBlockUnderZPK("1234", "1234567890123456", "CardBinValidation");
        verify(bankMiddlewareService, times(1)).callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class));
        verify(customerRepository, times(1)).findUsernameByCustomerId(123456L);
        verify(otpService, times(1)).generateOtp(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(OtpGenerateRequest.class));
    }

    @Test
    void testValidateCardBin_NoBinFound() {
        // Given
        when(cardBinMasterRepository.findByBin("12345678")).thenReturn(Collections.emptyList());

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());

        verify(cardBinMasterRepository, times(1)).findByBin("12345678");
        verify(hsmEncryptor, never()).generatePinBlockUnderZPK(anyString(), anyString(), anyString());
    }

    @Test
    void testValidateCardBin_HSMEncryptionFailure() throws Exception {
        // Given
        when(cardBinMasterRepository.findByBin("12345678")).thenReturn(Arrays.asList(cardBinMaster));
        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString()))
                .thenThrow(new BARWAHSMEncryptionException("HSM001", "HSM encryption failed"));

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);
        System.out.println(response);
        // Then
        assertNotNull(response);
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());

        verify(cardBinMasterRepository, times(1)).findByBin("12345678");
        verify(hsmEncryptor, times(1)).generatePinBlockUnderZPK("1234", "1234567890123456", "CardBinValidation");
        verify(bankMiddlewareService, never()).callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class));
    }

    @Test
    void testValidateCardBin_BankMiddlewareFailure() throws Exception {
        // Given
        when(cardBinMasterRepository.findByBin("12345678")).thenReturn(Arrays.asList(cardBinMaster));
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

        verify(cardBinMasterRepository, times(1)).findByBin("12345678");
        verify(hsmEncryptor, times(1)).generatePinBlockUnderZPK("1234", "1234567890123456", "CardBinValidation");
        verify(bankMiddlewareService, times(1)).callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class));
    }

    @Test
    void testValidateCardBin_CustomerNotFound() throws Exception {
        // Given
        when(cardBinMasterRepository.findByBin("12345678")).thenReturn(Arrays.asList(cardBinMaster));
        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString()))
                .thenReturn("ENCRYPTED_PIN");
        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class)))
                .thenReturn(bankResponse);
        when(customerRepository.findUsernameByCustomerId(123456L)).thenReturn(Optional.empty());

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());

        verify(customerRepository, times(1)).findUsernameByCustomerId(123456L);
        verify(otpService, never()).generateOtp(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(OtpGenerateRequest.class));
    }

    @Test
    void testValidateCardBin_InvalidCustomerNumber() throws Exception {
        // Given
        when(cardBinMasterRepository.findByBin("12345678")).thenReturn(Arrays.asList(cardBinMaster));
        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString()))
                .thenReturn("ENCRYPTED_PIN");
        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class)))
                .thenReturn(bankResponse);
        when(customerRepository.findUsernameByCustomerId(anyLong()))
                .thenThrow(new NumberFormatException("Invalid number"));

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());

        verify(customerRepository, times(1)).findUsernameByCustomerId(anyLong());
        verify(otpService, never()).generateOtp(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(OtpGenerateRequest.class));
    }

    @Test
    void testValidateCardBin_OTPGenerationFailure() throws Exception {
        // Given
        when(cardBinMasterRepository.findByBin("12345678")).thenReturn(Arrays.asList(cardBinMaster));
        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString()))
                .thenReturn("ENCRYPTED_PIN");
        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class)))
                .thenReturn(bankResponse);
        when(customerRepository.findUsernameByCustomerId(123456L)).thenReturn(Optional.of("testuser"));
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
    void testValidateCardBin_Exception() throws Exception {
        // Given
        when(cardBinMasterRepository.findByBin("12345678")).thenThrow(new RuntimeException("Database error"));

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());

        verify(cardBinMasterRepository, times(1)).findByBin("12345678");
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
    void testFindMatchingBin_With8DigitBin() {
        // Given
        when(cardBinMasterRepository.findByBin("12345678")).thenReturn(Arrays.asList(cardBinMaster));

        // When
        cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        verify(cardBinMasterRepository, times(1)).findByBin("12345678");
    }

    @Test
    void testFindMatchingBin_With7DigitBin() {
        // Given
        when(cardBinMasterRepository.findByBin("12345678")).thenReturn(Collections.emptyList());
        when(cardBinMasterRepository.findByBin("1234567")).thenReturn(Arrays.asList(cardBinMaster));

        // When
        cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        verify(cardBinMasterRepository, times(1)).findByBin("12345678");
        verify(cardBinMasterRepository, times(1)).findByBin("1234567");
    }

    @Test
    void testFindMatchingBin_With6DigitBin() {
        // Given
        when(cardBinMasterRepository.findByBin("12345678")).thenReturn(Collections.emptyList());
        when(cardBinMasterRepository.findByBin("1234567")).thenReturn(Collections.emptyList());
        when(cardBinMasterRepository.findByBin("123456")).thenReturn(Arrays.asList(cardBinMaster));

        // When
        cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request);

        // Then
        verify(cardBinMasterRepository, times(1)).findByBin("12345678");
        verify(cardBinMasterRepository, times(1)).findByBin("1234567");
        verify(cardBinMasterRepository, times(1)).findByBin("123456");
    }

    @Test
    void testFindMatchingBin_NullCardNumber() {
        // Given
        CardBinValidationRequest nullCardRequest = CardBinValidationRequest.builder()
                .cardNumber(null)
                .pin("1234")
                .build();

        // When
        GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", nullCardRequest);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());

        verify(cardBinMasterRepository, never()).findByBin(anyString());
    }
}