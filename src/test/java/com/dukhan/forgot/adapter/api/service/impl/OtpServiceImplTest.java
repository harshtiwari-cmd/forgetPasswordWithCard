package com.dukhan.forgot.adapter.api.service.impl;

import com.dukhan.forgot.domain.model.dto.DeviceInfo;
import com.dukhan.forgot.domain.model.dto.OtpGenerateRequest;
import com.dukhan.forgot.domain.model.dto.OtpGenerateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OtpServiceImpl otpService;

    private OtpGenerateRequest request;
    private OtpGenerateResponse successResponse;

    @BeforeEach
    void setUp() {
        // Set the OTP service URL using reflection
        ReflectionTestUtils.setField(otpService, "otpServiceUrl", "http://localhost:9096/rb-user-management/api/v1/otp/generate");

        request = OtpGenerateRequest.builder()
                .requestInfo(OtpGenerateRequest.RequestInfo.builder()
                        .action("login")
                        .rimNo("+97433333335")
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

        successResponse = OtpGenerateResponse.builder()
                .status(OtpGenerateResponse.Status.builder()
                        .code("000000")
                        .description("SUCCESS")
                        .build())
                .data(OtpGenerateResponse.OtpData.builder()
                        .mobileNumber("*******3335")
                        .message("OTP generated successfully and sent to registered mobile number")
                        .build())
                .build();
    }

    @Test
    void testGenerateOtp_Success() {
        // Given
        ResponseEntity<OtpGenerateResponse> responseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class)))
                .thenReturn(responseEntity);

        // When
        OtpGenerateResponse response = otpService.generateOtp(
                "DEFAULT", "WEB", "en", "OTP_SERVICE", "LOGIN_SCREEN", "AUTH_MODULE", "OTP_SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertEquals("000000", response.getStatus().getCode());
        assertEquals("SUCCESS", response.getStatus().getDescription());
        assertNotNull(response.getData());
        assertEquals("*******3335", response.getData().getMobileNumber());
        assertEquals("OTP generated successfully and sent to registered mobile number", response.getData().getMessage());

        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class));
    }

    @Test
    void testGenerateOtp_HttpError() {
        // Given
        ResponseEntity<OtpGenerateResponse> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class)))
                .thenReturn(responseEntity);

        // When
        OtpGenerateResponse response = otpService.generateOtp(
                "DEFAULT", "WEB", "en", "OTP_SERVICE", "LOGIN_SCREEN", "AUTH_MODULE", "OTP_SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertEquals("999999", response.getStatus().getCode());
        assertEquals("FAILED", response.getStatus().getDescription());
        assertNotNull(response.getData());
        assertEquals("OTP generation failed", response.getData().getMessage());

        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class));
    }

    @Test
    void testGenerateOtp_NullResponse() {
        // Given
        ResponseEntity<OtpGenerateResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class)))
                .thenReturn(responseEntity);

        // When
        OtpGenerateResponse response = otpService.generateOtp(
                "DEFAULT", "WEB", "en", "OTP_SERVICE", "LOGIN_SCREEN", "AUTH_MODULE", "OTP_SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertEquals("999999", response.getStatus().getCode());
        assertEquals("FAILED", response.getStatus().getDescription());
        assertNotNull(response.getData());
        assertEquals("OTP generation failed", response.getData().getMessage());

        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class));
    }

    @Test
    void testGenerateOtp_Exception() {
        // Given
        when(restTemplate.exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class)))
                .thenThrow(new RuntimeException("Network error"));

        // When
        OtpGenerateResponse response = otpService.generateOtp(
                "DEFAULT", "WEB", "en", "OTP_SERVICE", "LOGIN_SCREEN", "AUTH_MODULE", "OTP_SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertEquals("999999", response.getStatus().getCode());
        assertEquals("FAILED", response.getStatus().getDescription());
        assertNotNull(response.getData());
        assertEquals("OTP generation service unavailable", response.getData().getMessage());

        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class));
    }

    @Test
    void testGenerateOtp_WithNullParameters() {
        // Given
        ResponseEntity<OtpGenerateResponse> responseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class)))
                .thenReturn(responseEntity);

        // When
        OtpGenerateResponse response = otpService.generateOtp(
                null, null, null, null, null, null, null, request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertEquals("000000", response.getStatus().getCode());
        assertEquals("SUCCESS", response.getStatus().getDescription());

        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class));
    }

    @Test
    void testGenerateOtp_WithCustomParameters() {
        // Given
        ResponseEntity<OtpGenerateResponse> responseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class)))
                .thenReturn(responseEntity);

        // When
        OtpGenerateResponse response = otpService.generateOtp(
                "BKR", "MOB", "ar", "CUSTOM_SERVICE", "CUSTOM_SCREEN", "CUSTOM_MODULE", "CUSTOM_SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertEquals("000000", response.getStatus().getCode());
        assertEquals("SUCCESS", response.getStatus().getDescription());

        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class));
    }

    @Test
    void testGenerateOtp_ServiceUnavailable() {
        // Given
        when(restTemplate.exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        // When
        OtpGenerateResponse response = otpService.generateOtp(
                "DEFAULT", "WEB", "en", "OTP_SERVICE", "LOGIN_SCREEN", "AUTH_MODULE", "OTP_SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertEquals("999999", response.getStatus().getCode());
        assertEquals("FAILED", response.getStatus().getDescription());
        assertNotNull(response.getData());
        assertEquals("OTP generation service unavailable", response.getData().getMessage());

        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class));
    }

    @Test
    void testGenerateOtp_TimeoutException() {
        // Given
        when(restTemplate.exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class)))
                .thenThrow(new RuntimeException("Read timeout"));

        // When
        OtpGenerateResponse response = otpService.generateOtp(
                "DEFAULT", "WEB", "en", "OTP_SERVICE", "LOGIN_SCREEN", "AUTH_MODULE", "OTP_SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertEquals("999999", response.getStatus().getCode());
        assertEquals("FAILED", response.getStatus().getDescription());
        assertNotNull(response.getData());
        assertEquals("OTP generation service unavailable", response.getData().getMessage());

        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class));
    }

    @Test
    void testGenerateOtp_InvalidResponse() {
        // Given
        OtpGenerateResponse invalidResponse = OtpGenerateResponse.builder()
                .status(OtpGenerateResponse.Status.builder()
                        .code("999999")
                        .description("FAILED")
                        .build())
                .data(OtpGenerateResponse.OtpData.builder()
                        .mobileNumber("*******3335")
                        .message("Invalid request")
                        .build())
                .build();

        ResponseEntity<OtpGenerateResponse> responseEntity = new ResponseEntity<>(invalidResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class)))
                .thenReturn(responseEntity);

        // When
        OtpGenerateResponse response = otpService.generateOtp(
                "DEFAULT", "WEB", "en", "OTP_SERVICE", "LOGIN_SCREEN", "AUTH_MODULE", "OTP_SUBMODULE", request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertEquals("999999", response.getStatus().getCode());
        assertEquals("FAILED", response.getStatus().getDescription());
        assertNotNull(response.getData());
        assertEquals("Invalid request", response.getData().getMessage());

        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(OtpGenerateResponse.class));
    }
}
