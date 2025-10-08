package com.dukhan.forgot.adapter.api.controller;

import com.dukhan.forgot.adapter.api.service.CardBinValidationService;
import com.dukhan.forgot.domain.model.dto.CardBinValidationRequest;
import com.dukhan.forgot.domain.model.dto.CardBinValidationResponse;
import com.dukhan.forgot.domain.model.entity.CardBinMaster;
import com.dukhan.forgot.infrastructure.common.AppConstant;
import com.dukhan.forgot.infrastructure.common.GenericResponse;
import com.dukhan.forgot.infrastructure.common.ResultUtilVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardBinValidationControllerTest {

    private CardBinValidationService service;
    private CardBinValidationController controller;

    @BeforeEach
    void setUp() {
        service = mock(CardBinValidationService.class);
        controller = new CardBinValidationController(service);
    }

    @Test
    void testValidateCardBin_Success() {
        CardBinValidationRequest request = new CardBinValidationRequest("1234567890123456", "1234");

        CardBinValidationResponse responseData = new CardBinValidationResponse(
                true, "Success", "123456", "CREDIT", "VISA", "CODE1",
                "ENCRYPTED_PIN", "<xml/>", "<xml/>", null
        );

        GenericResponse<CardBinValidationResponse> serviceResponse =
                new GenericResponse<>(responseData, new ResultUtilVO(AppConstant.RESULT_CODE, "SUCCESS"));

        when(service.validateCardBin(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(serviceResponse);

        GenericResponse<CardBinValidationResponse> response = controller.validateCardBin(
                "BKR", "MOB", "E", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request
        );

        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertEquals(AppConstant.RESULT_CODE, response.getStatus().getCode());
        assertNotNull(response.getData());
        assertEquals("123456", response.getData().getBin());
        assertEquals("ENCRYPTED_PIN", response.getData().getEncryptedPin());
    }

    @Test
    void testValidateCardBin_Error() {
        CardBinValidationRequest request = new CardBinValidationRequest("1234567890123456", "1234");

        GenericResponse<CardBinValidationResponse> serviceResponse =
                GenericResponse.error("G-0001", "Card not valid");

        when(service.validateCardBin(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(serviceResponse);

        GenericResponse<CardBinValidationResponse> response = controller.validateCardBin(
                "BKR", "MOB", "E", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", request
        );

        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertEquals("G-0001", response.getStatus().getCode());
        assertNull(response.getData());
    }

    @Test
    void testGetActiveBins_Success() {
        // create some sample CardBinMaster objects
        CardBinMaster bin1 = new CardBinMaster("CODE1", "123456", "CREDIT", "VISA", "ACTIVE");
        CardBinMaster bin2 = new CardBinMaster("CODE2", "654321", "DEBIT", "MASTERCARD", "ACTIVE");

        List<CardBinMaster> activeBins = List.of(bin1, bin2);
        GenericResponse<List<CardBinMaster>> serviceResponse = GenericResponse.success(activeBins);

        when(service.getActiveBins()).thenReturn(serviceResponse);

        GenericResponse<List<CardBinMaster>> response = controller.getActiveBins();

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        assertEquals("123456", response.getData().get(0).getBin());
        assertEquals(AppConstant.RESULT_CODE, response.getStatus().getCode());
    }

    @Test
    void testGetActiveBins_NoData() {
        GenericResponse<List<CardBinMaster>> serviceResponse = GenericResponse.successNoData(Collections.emptyList());

        when(service.getActiveBins()).thenReturn(serviceResponse);

        GenericResponse<List<CardBinMaster>> response = controller.getActiveBins();

        assertNotNull(response);
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty());
        assertEquals(AppConstant.NO_DATA_CODE, response.getStatus().getCode());
    }


}
