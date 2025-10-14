package com.dukhan.forgot.adapter.api.service.impl;


import com.dukhan.forgot.adapter.api.service.CardValidationService;
import com.dukhan.forgot.domain.model.dto.CardValidationRequest;
import com.dukhan.forgot.domain.model.dto.CardValidationResponse;
import com.dukhan.forgot.domain.model.dto.GenericResponse;
import com.dukhan.forgot.infrastructure.common.exception.BARWAHSMEncryptionException;
import com.dukhan.forgot.infrastructure.common.exception.BARWAHSMParsingException;
import com.dukhan.forgot.infrastructure.common.exception.BarwaHSMCommuicationException;
import com.dukhan.forgot.infrastructure.common.hsm.HSMEncryptorManagerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardValidationServiceImplTest {

    private HSMEncryptorManagerImpl hsmEncryptor;
    private CardValidationService service;

    @BeforeEach
    void setUp() {
        hsmEncryptor = mock(HSMEncryptorManagerImpl.class);
        service = new CardValidationServiceImpl(hsmEncryptor);
    }

    @Test
    void testValidateCard_Success() throws Exception {
        CardValidationRequest request = new CardValidationRequest("1234567890123456", "1234");

        // Mock successful encryption
        when(hsmEncryptor.generatePinBlockUnderZPK("1234", "1234567890123456"))
                .thenReturn("ENCRYPTED_PIN");

        GenericResponse<CardValidationResponse> response = service.validateCard(request);

        assertNotNull(response);
        assertEquals("0000", response.getStatus().getCode());
        assertEquals("SUCCESS", response.getStatus().getDescription());
        assertNotNull(response.getData());
        assertEquals("ENCRYPTED_PIN", response.getData().getEncryptedPin());
    }

    @Test
    void testValidateCard_BarwaHSMCommuicationException() throws Exception {
        CardValidationRequest request = new CardValidationRequest("1234567890123456", "1234");

        // Throw HSM communication exception
        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString()))
                .thenThrow(new BarwaHSMCommuicationException("G-0001", "Communication Error"));

        GenericResponse<CardValidationResponse> response = service.validateCard(request);

        assertEquals("G-0001", response.getStatus().getCode());
        assertEquals("G-0001:Communication Error", response.getStatus().getDescription());
        assertNull(response.getData());
    }

    @Test
    void testValidateCard_BARWAHSMEncryptionException() throws Exception {
        CardValidationRequest request = new CardValidationRequest("1234567890123456", "1234");

        // Throw encryption exception
        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString()))
                .thenThrow(new BARWAHSMEncryptionException("G-0001", "Encryption Error"));

        GenericResponse<CardValidationResponse> response = service.validateCard(request);

        assertEquals("G-0001", response.getStatus().getCode());
        assertEquals("G-0001:Encryption Error", response.getStatus().getDescription());
        assertNull(response.getData());
    }

    @Test
    void testValidateCard_BARWAHSMParsingException() throws Exception {
        CardValidationRequest request = new CardValidationRequest("1234567890123456", "1234");

        // Throw parsing exception
        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString()))
                .thenThrow(new BARWAHSMParsingException("G-0001", "Parsing Error"));

        GenericResponse<CardValidationResponse> response = service.validateCard(request);

        assertEquals("G-0001", response.getStatus().getCode());
        assertEquals("G-0001:Parsing Error", response.getStatus().getDescription());
        assertNull(response.getData());
    }

    @Test
    void testValidateCard_GenericException() throws Exception {
        CardValidationRequest request = new CardValidationRequest("1234567890123456", "1234");

        // Throw unexpected exception
        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString()))
                .thenThrow(new RuntimeException("Unexpected Error"));

        GenericResponse<CardValidationResponse> response = service.validateCard(request);

        assertEquals("G-0002", response.getStatus().getCode());
        assertEquals("Internal Server Error", response.getStatus().getDescription());
        assertNull(response.getData());
    }
}
