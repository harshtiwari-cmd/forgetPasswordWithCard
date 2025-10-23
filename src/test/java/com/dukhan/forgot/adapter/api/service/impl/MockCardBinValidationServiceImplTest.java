package com.dukhan.forgot.adapter.api.service.impl;

import com.dukhan.forgot.domain.model.dto.CardBinValidationRequest;
import com.dukhan.forgot.domain.model.dto.SimpleValidationResponse;
import com.dukhan.forgot.domain.model.entity.CardBinMaster;
import com.dukhan.forgot.infrastructure.common.GenericResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MockCardBinValidationServiceImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MockCardBinValidationServiceImpl service;

    private CardBinValidationRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = CardBinValidationRequest.builder()
                .cardNumber("4203741234567889")
                .pin("1234")
                .build();
    }

    @Test
    void testValidateCardBin_nullCardNumber() throws Exception {

        validRequest.setCardNumber(null);
        // Arrange
        GenericResponse<SimpleValidationResponse> mockResponse = GenericResponse.success(
                new SimpleValidationResponse("12345", "John Doe", true)
        );

        // Mock objectMapper to return the above response
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(mockResponse);

        // Act
        GenericResponse<SimpleValidationResponse> response = service.validateCardBin(
                "unit1", "channel1", "EN", "service1", "screen1", "module1", "subModule1", validRequest
        );

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals("12345", response.getData().getRimNumber());
        assertTrue(response.getData().isOtp());
        assertEquals("John Doe", response.getData().getUserName());
        assertEquals("000000", response.getStatus().getCode());
        assertEquals("SUCCESS", response.getStatus().getDescription());

        verify(objectMapper, times(1)).readValue(any(InputStream.class), any(TypeReference.class));
    }

    @Test
    void testValidateCardBin_PositiveResponse() throws Exception {

        // Arrange
        GenericResponse<SimpleValidationResponse> mockResponse = GenericResponse.success(
                new SimpleValidationResponse("12345", "John Doe", true)
        );

        // Mock objectMapper to return the above response
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(mockResponse);

        // Act
        GenericResponse<SimpleValidationResponse> response = service.validateCardBin(
                "unit1", "channel1", "EN", "service1", "screen1", "module1", "subModule1", validRequest
        );

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals("12345", response.getData().getRimNumber());
        assertTrue(response.getData().isOtp());
        assertEquals("John Doe", response.getData().getUserName());
        assertEquals("SUCCESS", response.getStatus().getDescription());

        verify(objectMapper, times(1)).readValue(any(InputStream.class), any(TypeReference.class));
    }

    @Test
    void testValidateCardBin_BinNotValid() throws IOException {

        validRequest.setCardNumber("3209741234567889");

        GenericResponse<Object> mockResponse = GenericResponse.error("000400", "BIN_NOT_VALID");

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(mockResponse);

        GenericResponse<SimpleValidationResponse> response = service.validateCardBin(
                "unit1", "channel1", "EN", "service1", "screen1", "module1", "subModule1", validRequest
        );

        assertNotNull(response);
        assertEquals("000400", response.getStatus().getCode());
        assertEquals("BIN_NOT_VALID", response.getStatus().getDescription());

    }

    @Test
    void testValidateCardBin_cardNotValid() throws IOException {

        validRequest.setCardNumber("1003741234567889");

        GenericResponse<Object> mockResponse = GenericResponse.error("000400", "CARD_NOT_VALID_MUST_USE_DEBIT");

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(mockResponse);

        GenericResponse<SimpleValidationResponse> response = service.validateCardBin(
                "unit1", "channel1", "EN", "service1", "screen1", "module1", "subModule1", validRequest
        );

        assertNotNull(response);
        assertEquals("000400", response.getStatus().getCode());
        assertEquals("CARD_NOT_VALID_MUST_USE_DEBIT", response.getStatus().getDescription());


    }

    @Test
    void testValidateCardBin_userBlocked() throws IOException {

        validRequest.setCardNumber("9003901234567889");

        GenericResponse<Object> mockResponse = GenericResponse.error("000400", "USER_BLOCKED_CONTACT_BANK");

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(mockResponse);

        GenericResponse<SimpleValidationResponse> response = service.validateCardBin(
                "unit1", "channel1", "EN", "service1", "screen1", "module1", "subModule1", validRequest
        );

        assertNotNull(response);
        assertEquals("000400", response.getStatus().getCode());
        assertEquals("USER_BLOCKED_CONTACT_BANK", response.getStatus().getDescription());

    }

    @Test
    void testValidateCardBin_otpLimitExceeded() throws IOException {

        validRequest.setCardNumber("9898741234567889");

        GenericResponse<Object> mockResponse = GenericResponse.error("000400", "USER_BLOCKED_OTP_LIMIT_EXCEEDED");

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(mockResponse);

        GenericResponse<SimpleValidationResponse> response = service.validateCardBin(
                "unit1", "channel1", "EN", "service1", "screen1", "module1", "subModule1", validRequest
        );

        assertNotNull(response);
        assertEquals("000400", response.getStatus().getCode());
        assertEquals("USER_BLOCKED_OTP_LIMIT_EXCEEDED", response.getStatus().getDescription());

    }

    @Test
    void testValidateCardBin_invalidAttempts() throws IOException {

        validRequest.setCardNumber("8080741234567889");

        GenericResponse<Object> mockResponse = GenericResponse.error("000400", "INVALID_ATTEMPTS_LIMIT_EXCEEDED");

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(mockResponse);

        GenericResponse<SimpleValidationResponse> response = service.validateCardBin(
                "unit1", "channel1", "EN", "service1", "screen1", "module1", "subModule1", validRequest
        );

        assertNotNull(response);
        assertEquals("000400", response.getStatus().getCode());
        assertEquals("INVALID_ATTEMPTS_LIMIT_EXCEEDED", response.getStatus().getDescription());

    }

    @Test
    void testValidateCardBin_retryAfter24Hourse() throws IOException {

        validRequest.setCardNumber("6060741234567889");

        GenericResponse<Object> mockResponse = GenericResponse.error("CARD002", "RETRY_AFTER_24_HOURS");

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(mockResponse);

        GenericResponse<SimpleValidationResponse> response = service.validateCardBin(
                "unit1", "channel1", "EN", "service1", "screen1", "module1", "subModule1", validRequest
        );

        assertNotNull(response);
        assertEquals("CARD002", response.getStatus().getCode());
        assertEquals("RETRY_AFTER_24_HOURS", response.getStatus().getDescription());

    }

    @Test
    void testValidateCardBin_ErrorLoadingFile() throws Exception {
        // Arrange
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenThrow(new IOException("File not found"));

        // Act
        GenericResponse<SimpleValidationResponse> response = service.validateCardBin(
                "unit", "channel", "EN", "service", "screen", "module", "subModule", validRequest
        );

        // Assert
        assertNotNull(response);
        assertNull(response.getData());
        assertEquals("MOCK_ERROR", response.getStatus().getCode());
        assertEquals("Failed to load mock response", response.getStatus().getDescription());
    }

    @Test
    void testValidateCardBin_DefaultFileUsedForUnknownCard() throws Exception {
        // Arrange
        CardBinValidationRequest unknownCard = CardBinValidationRequest.builder()
                .cardNumber("9999999999999999")
                .pin("1234")
                .build();

        GenericResponse<SimpleValidationResponse> mockResponse = GenericResponse.success(
                new SimpleValidationResponse("00000", "Default User", false)
        );
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(mockResponse);

        // Act
        GenericResponse<SimpleValidationResponse> response = service.validateCardBin(
                "u", "c", "EN", "s", "sc", "m", "sub", unknownCard
        );

        // Assert
        assertEquals("00000", response.getData().getRimNumber());
        assertEquals("Default User", response.getData().getUserName());
    }

    @Test
    void testGetActiveBins_Success() throws Exception {
        // Arrange
        GenericResponse<List<CardBinMaster>> mockResponse = GenericResponse.success(List.of(
                new CardBinMaster(), new CardBinMaster()
        ));
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(mockResponse);

        // Act
        GenericResponse<List<CardBinMaster>> response = service.getActiveBins();

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        assertEquals("SUCCESS", response.getStatus().getDescription());

        verify(objectMapper, times(1)).readValue(any(InputStream.class), any(TypeReference.class));
    }

    @Test
    void testGetActiveBins_Error() throws Exception {
        // Arrange
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenThrow(new IOException("Error loading file"));

        // Act
        GenericResponse<List<CardBinMaster>> response = service.getActiveBins();

        // Assert
        assertNull(response.getData());
        assertEquals("MOCK_ERROR", response.getStatus().getCode());
        assertEquals("Failed to load mock response", response.getStatus().getDescription());
    }
}
