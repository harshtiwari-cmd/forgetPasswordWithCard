package com.dukhan.forgot.adapter.api.controller;

import com.dukhan.forgot.adapter.api.service.CardBinValidationService;
import com.dukhan.forgot.domain.model.dto.*;
import com.dukhan.forgot.domain.model.entity.CardBinMaster;
import com.dukhan.forgot.infrastructure.common.AppConstant;
import com.dukhan.forgot.infrastructure.common.GenericResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardBinValidationControllerTest {

    @Mock
    private CardBinValidationService cardBinValidationService;

    @InjectMocks
    private CardBinValidationController controller;

    private CardBinValidationWrapper wrapper;
    private CardBinValidationRequest request;
    private SimpleValidationResponse validationResponse;

    @BeforeEach
    void setUp() {
        request = CardBinValidationRequest.builder()
                .cardNumber("1234567890123456")
                .pin("1234")
                .build();

        wrapper = CardBinValidationWrapper.builder()
                .requestInfo(request)
                .build();

        validationResponse = SimpleValidationResponse.builder()
                .rimNumber("123456")
                .userName("testuser")
                .otp(true)
                .build();
    }

    @Test
    void testValidateCardBin_Success() {
        // Given
        GenericResponse<SimpleValidationResponse> serviceResponse = GenericResponse.success(validationResponse);
        when(cardBinValidationService.validateCardBin(anyString(), anyString(), anyString(), 
                anyString(), anyString(), anyString(), anyString(), any(CardBinValidationRequest.class)))
                .thenReturn(serviceResponse);

        // When
        ResponseEntity<GenericResponse<SimpleValidationResponse>> response = controller.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", wrapper);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(AppConstant.RESULT_CODE, response.getBody().getStatus().getCode());
        assertNotNull(response.getBody().getData());
        assertEquals("123456", response.getBody().getData().getRimNumber());
        assertEquals("testuser", response.getBody().getData().getUserName());
        assertTrue(response.getBody().getData().isOtp());

        verify(cardBinValidationService, times(1)).validateCardBin(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), any(CardBinValidationRequest.class));
    }

    @Test
    void testValidateCardBin_ServiceReturnsError() {
        // Given
        GenericResponse<SimpleValidationResponse> serviceResponse = GenericResponse.error(
                AppConstant.VALIDATION_FAILURE_CODE, AppConstant.VALIDATION_FAILURE_DESC);
        when(cardBinValidationService.validateCardBin(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), any(CardBinValidationRequest.class)))
                .thenReturn(serviceResponse);

        // When
        ResponseEntity<GenericResponse<SimpleValidationResponse>> response = controller.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", wrapper);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(AppConstant.VALIDATION_FAILURE_CODE, response.getBody().getStatus().getCode());
        assertEquals(AppConstant.VALIDATION_FAILURE_DESC, response.getBody().getStatus().getDescription());

        verify(cardBinValidationService, times(1)).validateCardBin(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), any(CardBinValidationRequest.class));
    }

    @Test
    void testValidateCardBin_ServiceReturnsNull() {
        // Given
        when(cardBinValidationService.validateCardBin(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), any(CardBinValidationRequest.class)))
                .thenReturn(null);

        // When
        ResponseEntity<GenericResponse<SimpleValidationResponse>> response = controller.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", wrapper);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(AppConstant.VALIDATION_FAILURE_CODE, response.getBody().getStatus().getCode());
        assertEquals(AppConstant.VALIDATION_FAILURE_DESC, response.getBody().getStatus().getDescription());
    }

    @Test
    void testValidateCardBin_ServiceReturnsNullStatus() {
        // Given
        GenericResponse<SimpleValidationResponse> serviceResponse = new GenericResponse<>();
        serviceResponse.setStatus(null);
        when(cardBinValidationService.validateCardBin(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), any(CardBinValidationRequest.class)))
                .thenReturn(serviceResponse);

        // When
        ResponseEntity<GenericResponse<SimpleValidationResponse>> response = controller.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", wrapper);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(AppConstant.VALIDATION_FAILURE_CODE, response.getBody().getStatus().getCode());
        assertEquals(AppConstant.VALIDATION_FAILURE_DESC, response.getBody().getStatus().getDescription());
    }

    @Test
    void testValidateCardBin_ServiceReturnsInvalidData() {
        // Given
        SimpleValidationResponse invalidResponse = SimpleValidationResponse.builder()
                .rimNumber(null)
                .userName(null)
                .otp(false)
                .build();
        GenericResponse<SimpleValidationResponse> serviceResponse = GenericResponse.success(invalidResponse);
        when(cardBinValidationService.validateCardBin(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), any(CardBinValidationRequest.class)))
                .thenReturn(serviceResponse);

        // When
        ResponseEntity<GenericResponse<SimpleValidationResponse>> response = controller.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", wrapper);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(AppConstant.VALIDATION_FAILURE_CODE, response.getBody().getStatus().getCode());
        assertEquals(AppConstant.VALIDATION_FAILURE_DESC, response.getBody().getStatus().getDescription());
    }

    @Test
    void testValidateCardBin_ServiceThrowsException() {
        // Given
        when(cardBinValidationService.validateCardBin(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), any(CardBinValidationRequest.class)))
                .thenThrow(new RuntimeException("Service error"));

        // When
        ResponseEntity<GenericResponse<SimpleValidationResponse>> response = controller.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", wrapper);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(AppConstant.VALIDATION_FAILURE_CODE, response.getBody().getStatus().getCode());
        assertEquals(AppConstant.VALIDATION_FAILURE_DESC, response.getBody().getStatus().getDescription());
    }

    @Test
    void testGetActiveBins_Success() {
        // Given
        CardBinMaster bin1 = CardBinMaster.builder()
                .code("CODE1")
                .bin("123456")
                .productType("CREDIT")
                .cardType("VISA")
                .status("ACTIVE")
                .build();

        CardBinMaster bin2 = CardBinMaster.builder()
                .code("CODE2")
                .bin("654321")
                .productType("DEBIT")
                .cardType("MASTERCARD")
                .status("ACTIVE")
                .build();

        List<CardBinMaster> activeBins = List.of(bin1, bin2);
        GenericResponse<List<CardBinMaster>> serviceResponse = GenericResponse.success(activeBins);
        when(cardBinValidationService.getActiveBins()).thenReturn(serviceResponse);

        CardBinAllWrapper wrapper = CardBinAllWrapper.builder()
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

        // When
        GenericResponse<List<CardBinMaster>> response = controller.getActiveBins(
                "SERVICE", "MODULE", "SUBMODULE", "SCREEN", "WEB", "en", wrapper);

        // Then
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        assertEquals("123456", response.getData().get(0).getBin());
        assertEquals(AppConstant.RESULT_CODE, response.getStatus().getCode());

        verify(cardBinValidationService, times(1)).getActiveBins();
    }

    @Test
    void testGetActiveBins_NoData() {
        // Given
        GenericResponse<List<CardBinMaster>> serviceResponse = GenericResponse.successNoData(Collections.emptyList());
        when(cardBinValidationService.getActiveBins()).thenReturn(serviceResponse);

        CardBinAllWrapper wrapper = CardBinAllWrapper.builder()
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

        // When
        GenericResponse<List<CardBinMaster>> response = controller.getActiveBins(
                "SERVICE", "MODULE", "SUBMODULE", "SCREEN", "WEB", "en", wrapper);

        // Then
        assertNotNull(response);
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty());
        assertEquals(AppConstant.NO_DATA_CODE, response.getStatus().getCode());

        verify(cardBinValidationService, times(1)).getActiveBins();
    }

    @Test
    void testGetActiveBins_NullWrapper() {
        // When
        GenericResponse<List<CardBinMaster>> response = controller.getActiveBins(
                "SERVICE", "MODULE", "SUBMODULE", "SCREEN", "WEB", "en", null);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals("Request body is required", response.getStatus().getDescription());
    }

    @Test
    void testGetActiveBins_NullDeviceInfo() {
        // Given
        CardBinAllWrapper wrapper = CardBinAllWrapper.builder()
                .deviceInfo(null)
                .build();

        // When
        GenericResponse<List<CardBinMaster>> response = controller.getActiveBins(
                "SERVICE", "MODULE", "SUBMODULE", "SCREEN", "WEB", "en", wrapper);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals("Device information is required", response.getStatus().getDescription());
    }

    @Test
    void testGetActiveBins_ServiceReturnsError() {
        // Given
        GenericResponse<List<CardBinMaster>> serviceResponse = GenericResponse.error(
                AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC);
        when(cardBinValidationService.getActiveBins()).thenReturn(serviceResponse);

        CardBinAllWrapper wrapper = CardBinAllWrapper.builder()
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

        // When
        GenericResponse<List<CardBinMaster>> response = controller.getActiveBins(
                "SERVICE", "MODULE", "SUBMODULE", "SCREEN", "WEB", "en", wrapper);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());

        verify(cardBinValidationService, times(1)).getActiveBins();
    }

    @Test
    void testGetActiveBins_ServiceThrowsException() {
        // Given
        when(cardBinValidationService.getActiveBins()).thenThrow(new RuntimeException("Database error"));

        CardBinAllWrapper wrapper = CardBinAllWrapper.builder()
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

        // When
        GenericResponse<List<CardBinMaster>> response = controller.getActiveBins(
                "SERVICE", "MODULE", "SUBMODULE", "SCREEN", "WEB", "en", wrapper);

        // Then
        assertNotNull(response);
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());

        verify(cardBinValidationService, times(1)).getActiveBins();
    }

    @Test
    void testMaskCardNumber_NullCardNumber() {
        // When
        String result = controller.maskCardNumber(null);

        // Then
        assertEquals("****", result);
    }

    @Test
    void testMaskCardNumber_ShortCardNumber() {
        // When
        String result = controller.maskCardNumber("123");

        // Then
        assertEquals("****", result);
    }

    @Test
    void testMaskCardNumber_ValidCardNumber() {
        // When
        String result = controller.maskCardNumber("1234567890123456");

        // Then
        assertEquals("1234****3456", result);
    }
}