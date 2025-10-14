package com.dukhan.forgot.adapter.api.controller;

import com.dukhan.forgot.adapter.api.service.CardValidationService;
import com.dukhan.forgot.domain.model.dto.CardValidationRequest;
import com.dukhan.forgot.domain.model.dto.CardValidationResponse;
import com.dukhan.forgot.domain.model.dto.GenericResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CardValidationControllerTest {

    private CardValidationService service;
    private CardValidationController controller;

    @BeforeEach
    void setUp() {
        service = mock(CardValidationService.class);
        controller = new CardValidationController(service);
        // inject mock
    }

    @Test
    void testValidateCard_Success() {
        CardValidationRequest request = new CardValidationRequest();
        request.setCardNumber("1234567890123456");
        request.setPin("1234");

        CardValidationResponse responseData = new CardValidationResponse();
        responseData.setEncryptedPin("ENCRYPTED_PIN");

        GenericResponse<CardValidationResponse> serviceResponse = new GenericResponse<>();
        serviceResponse.setData(responseData);

        // Mock the service
        when(service.validateCard(request)).thenReturn(serviceResponse);

        // Call controller
        GenericResponse<CardValidationResponse> response = controller.validateCard(request);

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals("ENCRYPTED_PIN", response.getData().getEncryptedPin());
    }

    @Test
    void testValidateCard_NullResponse() {
        CardValidationRequest request = new CardValidationRequest();
        request.setCardNumber("1234567890123456");
        request.setPin("1234");

        // Service returns null response data
        GenericResponse<CardValidationResponse> serviceResponse = new GenericResponse<>();
        serviceResponse.setData(null);

        when(service.validateCard(request)).thenReturn(serviceResponse);

        GenericResponse<CardValidationResponse> response = controller.validateCard(request);

        assertNotNull(response);
        assertNull(response.getData());
    }
}
