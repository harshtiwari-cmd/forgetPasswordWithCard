package com.dukhan.forgot.adapter.api.service.impl;


import com.dukhan.forgot.adapter.api.service.XmlConversionService;
import com.dukhan.forgot.domain.model.dto.CardBinValidationRequest;
import com.dukhan.forgot.domain.model.dto.CardBinValidationResponse;
//import com.dukhan.forgot.domain.model.dto.GenericResponse;
import com.dukhan.forgot.domain.model.entity.CardBinMaster;
import com.dukhan.forgot.infrastructure.common.AppConstant;
import com.dukhan.forgot.infrastructure.common.GenericResponse;

import com.dukhan.forgot.domain.repository.CardBinMasterRepository;
import com.dukhan.forgot.infrastructure.common.exception.BarwaHSMCommuicationException;
import com.dukhan.forgot.infrastructure.common.hsm.HSMEncryptorManagerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardBinValidationServiceImplTest {

    private CardBinMasterRepository cardBinMasterRepository;
    private HSMEncryptorManagerImpl hsmEncryptor;
    private XmlConversionService xmlConversionService;
    private CardBinValidationServiceImpl service;

    @BeforeEach
    void setUp() {
        cardBinMasterRepository = mock(CardBinMasterRepository.class);
        hsmEncryptor = mock(HSMEncryptorManagerImpl.class);
        xmlConversionService = mock(XmlConversionService.class);

        service = new CardBinValidationServiceImpl(hsmEncryptor, cardBinMasterRepository, xmlConversionService);
    }
//
//    @Test
//    void testValidateCardBin_SuccessWithMock() throws Exception {
//        CardBinValidationRequest request = new CardBinValidationRequest("1234567890123456", "1234");
//
//        CardBinMaster binMaster = new CardBinMaster();
//        binMaster.setBin("123456");
//        binMaster.setProductType("CREDIT");
//        binMaster.setCardType("VISA");
//        binMaster.setCode("CODE1");
//
//        when(cardBinMasterRepository.findByBin("123456")).thenReturn(List.of(binMaster));
//        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString())).thenReturn("ENCRYPTED_PIN");
//        when(xmlConversionService.convertDcardPinVerificationToXml(any(), any())).thenReturn("<mockXml/>");
//
//        GenericResponse<CardBinValidationResponse> response = service.validateCardBin(
//                "BKR", "MOB", "E", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request
//        );
//
//        assertNotNull(response);
//        assertTrue(response.getData().isValid());
//        assertEquals("123456", response.getData().getBin());
//        assertEquals("ENCRYPTED_PIN", response.getData().getEncryptedPin());
//    }

    @Test
    void testValidateCardBin_CardNotFound() {
        CardBinValidationRequest request = new CardBinValidationRequest("1234567890123456", "1234");

        when(cardBinMasterRepository.findByBin(anyString())).thenReturn(Collections.emptyList());

        GenericResponse<CardBinValidationResponse> response = service.validateCardBin(
                "BKR", "MOB", "E", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request
        );

        assertNotNull(response);
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals("Card not valid", response.getStatus().getDescription());
        assertNull(response.getData()); // data is null in error cases
    }

    @Test
    void testValidateCardBin_HSMExceptions() throws Exception {
        CardBinValidationRequest request = new CardBinValidationRequest("1234567890123456", "1234");

        CardBinMaster binMaster = new CardBinMaster();
        binMaster.setBin("123456");
        binMaster.setProductType("CREDIT");
        binMaster.setCardType("VISA");
        binMaster.setCode("CODE1");

        when(cardBinMasterRepository.findByBin(anyString())).thenReturn(List.of(binMaster));

        // Simulate HSM exception
        when(hsmEncryptor.generatePinBlockUnderZPK(anyString(), anyString(), anyString()))
                .thenThrow(new BarwaHSMCommuicationException("G-0001", "Communication Error"));

        GenericResponse<CardBinValidationResponse> response = service.validateCardBin(
                "BKR", "MOB", "E", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request
        );

        assertNotNull(response);
        // Check status instead of data
        assertEquals("HSM-001", response.getStatus().getCode());
        assertTrue(response.getStatus().getDescription().contains("Communication Error"));
        // data is null on error
        assertNull(response.getData());
    }


    @Test
    void testValidateCardBin_RuntimeException() throws Exception {
        CardBinValidationRequest request = new CardBinValidationRequest("1234567890123456", "1234");

        // Simulate database runtime exception
        when(cardBinMasterRepository.findByBin(anyString())).thenThrow(new RuntimeException("DB error"));

        GenericResponse<CardBinValidationResponse> response = service.validateCardBin(
                "BKR", "MOB", "E", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request
        );

        assertNotNull(response);
        // Check status instead of data
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());
        // data should be null on error
        assertNull(response.getData());
    }
}

