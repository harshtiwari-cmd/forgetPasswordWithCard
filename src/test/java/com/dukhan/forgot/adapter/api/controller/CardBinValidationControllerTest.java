package com.dukhan.forgot.adapter.api.controller;

import com.dukhan.forgot.adapter.api.service.CardBinValidationService;
import com.dukhan.forgot.domain.model.dto.CardBinValidationRequest;
import com.dukhan.forgot.domain.model.dto.CardBinValidationResponse;
import com.dukhan.forgot.domain.model.dto.CardBinValidationWrapper;
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
        // given
        CardBinValidationRequest request = new CardBinValidationRequest("1234567890123456", "1234");
        CardBinValidationWrapper wrapper = new CardBinValidationWrapper();
        wrapper.setRequestInfo(request);

        CardBinValidationResponse responseData = new CardBinValidationResponse(
                true, "Success", "123456", "CREDIT", "VISA", "CODE1",
                "ENCRYPTED_PIN", "<xml/>", "<xml/>", null
        );

        GenericResponse<CardBinValidationResponse> serviceResponse =
                new GenericResponse<>(responseData, new ResultUtilVO(AppConstant.RESULT_CODE, "SUCCESS"));

        when(service.validateCardBin(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(CardBinValidationRequest.class))
        ).thenReturn(serviceResponse);

        // when
        GenericResponse<CardBinValidationResponse> response = controller.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", wrapper
        );

        // then
        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertEquals(AppConstant.RESULT_CODE, response.getStatus().getCode());
        assertNotNull(response.getData());
        assertEquals("123456", response.getData().getBin());
        assertEquals("ENCRYPTED_PIN", response.getData().getEncryptedPin());
        verify(service, times(1)).validateCardBin(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), any(CardBinValidationRequest.class));
    }

    @Test
    void testValidateCardBin_Error() {
        // given
        CardBinValidationRequest request = new CardBinValidationRequest("1234567890123456", "1234");
        CardBinValidationWrapper wrapper = new CardBinValidationWrapper();
        wrapper.setRequestInfo(request);

        GenericResponse<CardBinValidationResponse> serviceResponse =
                GenericResponse.error("G-0001", "Card not valid");

        when(service.validateCardBin(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(CardBinValidationRequest.class))
        ).thenReturn(serviceResponse);

        // when
        GenericResponse<CardBinValidationResponse> response = controller.validateCardBin(
                "BKR", "MOB", "en-US", "SERVICE", "SCREEN", "MODULE", "SUBMODULE", wrapper
        );

        // then
        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertEquals("G-0001", response.getStatus().getCode());
        assertNull(response.getData());
        verify(service, times(1)).validateCardBin(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), any(CardBinValidationRequest.class));
    }

    @Test
    void testGetActiveBins_Success() {
        // given
        CardBinMaster bin1 = new CardBinMaster("CODE1", "123456", "CREDIT", "VISA", "ACTIVE");
        CardBinMaster bin2 = new CardBinMaster("CODE2", "654321", "DEBIT", "MASTERCARD", "ACTIVE");

        List<CardBinMaster> activeBins = List.of(bin1, bin2);
        GenericResponse<List<CardBinMaster>> serviceResponse = GenericResponse.success(activeBins);

        when(service.getActiveBins()).thenReturn(serviceResponse);

        // when
        GenericResponse<List<CardBinMaster>> response = controller.getActiveBins();

        // then
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        assertEquals("123456", response.getData().get(0).getBin());
        assertEquals(AppConstant.RESULT_CODE, response.getStatus().getCode());
    }

    @Test
    void testGetActiveBins_NoData() {
        // given
        GenericResponse<List<CardBinMaster>> serviceResponse = GenericResponse.successNoData(Collections.emptyList());
        when(service.getActiveBins()).thenReturn(serviceResponse);

        // when
        GenericResponse<List<CardBinMaster>> response = controller.getActiveBins();

        // then
        assertNotNull(response);
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty());
        assertEquals(AppConstant.NO_DATA_CODE, response.getStatus().getCode());
    }

    @Test
    void testGetActiveBins_Exception() {
        // given
        when(service.getActiveBins()).thenThrow(new RuntimeException("Database failure"));

        // when
        GenericResponse<List<CardBinMaster>> response = controller.getActiveBins();

        // then
        assertNotNull(response);
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());
    }
}
