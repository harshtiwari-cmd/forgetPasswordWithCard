package com.dukhan.forgot.adapter.api.controller;

import com.dukhan.forgot.adapter.api.service.CardBinValidationService;
import com.dukhan.forgot.domain.model.dto.CardBinValidationRequest;
import com.dukhan.forgot.domain.model.dto.CardBinValidationResponse;
import com.dukhan.forgot.domain.model.entity.CardBinMaster;
import com.dukhan.forgot.infrastructure.common.AppConstant;
import com.dukhan.forgot.infrastructure.common.GenericResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardBinValidationController.class)
public class CardBInValidationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CardBinValidationService validationService;

    @Test
    public void shouldReturnSuccessWhenBinIsValid() throws Exception {

        CardBinValidationResponse success = CardBinValidationResponse.success("420374", "ISLAMIC PLATINUM", "CREDIT", "401");

        GenericResponse<CardBinValidationResponse> response = GenericResponse.success(success);

        when(validationService.validateCardBin(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), any(CardBinValidationRequest.class)
        )).thenReturn(response);

        mockMvc.perform(post("/v2/card-bin/validate")
                        .header(AppConstant.UNIT, "UNIT1")
                        .header(AppConstant.HEADER_CHANNEL, "WEB")
                        .header(AppConstant.HEADER_ACCEPT_LANGUAGE, "en")
                        .header(AppConstant.SERVICEID, "SERVICE123")
                        .header(AppConstant.SCREEN_ID, "SCREEN1")
                        .header(AppConstant.MODULE_ID, "MODULE1")
                        .header(AppConstant.SUB_MODULE_ID, "SUBMODULE1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("420374000012345"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.message").value("Card is valid"))
                .andExpect(jsonPath("$.data.bin").value("420374"))
                .andExpect(jsonPath("$.data.code").value("401"))
                .andExpect(jsonPath("$.status.description").value("SUCCESS"));

    }

    @Test
    public void shouldReturnBadRequestWhenBinIsNot15Length() throws Exception {

        GenericResponse<CardBinValidationResponse> card_not_valid = GenericResponse.error(AppConstant.GEN_ERROR_CODE, "Card not valid");

        CardBinValidationRequest request = new CardBinValidationRequest();

        request.setCardNumber("12345");

        when(validationService.validateCardBin(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), any()
        )).thenReturn(card_not_valid);

        mockMvc.perform(post("/v2/card-bin/validate")
                        .header(AppConstant.UNIT, "UNIT1")
                        .header(AppConstant.HEADER_CHANNEL, "WEB")
                        .header(AppConstant.HEADER_ACCEPT_LANGUAGE, "en")
                        .header(AppConstant.SERVICEID, "SERVICE123")
                        .header(AppConstant.SCREEN_ID, "SCREEN1")
                        .header(AppConstant.MODULE_ID, "MODULE1")
                        .header(AppConstant.SUB_MODULE_ID, "SUBMODULE1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status.code").value("000001"))
                .andExpect(jsonPath("$.status.description").value("Card number must be at least 15 digits"));
    }


    @Test
    public void shouldReturn200WhenCardBinIsInvalid() throws Exception {
        GenericResponse<CardBinValidationResponse> card_not_valid = GenericResponse.error(AppConstant.GEN_ERROR_CODE, "Card not valid");

        CardBinValidationRequest request = new CardBinValidationRequest();

        request.setCardNumber("020374000012345");

        when(validationService.validateCardBin(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), any()
        )).thenReturn(card_not_valid);

        mockMvc.perform(post("/v2/card-bin/validate")
                        .header(AppConstant.UNIT, "UNIT1")
                        .header(AppConstant.HEADER_CHANNEL, "WEB")
                        .header(AppConstant.HEADER_ACCEPT_LANGUAGE, "en")
                        .header(AppConstant.SERVICEID, "SERVICE123")
                        .header(AppConstant.SCREEN_ID, "SCREEN1")
                        .header(AppConstant.MODULE_ID, "MODULE1")
                        .header(AppConstant.SUB_MODULE_ID, "SUBMODULE1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value("000001"))
                .andExpect(jsonPath("$.status.description").value("Card not valid"));
    }


    @Test
    public void shouldReturnBadRequestWhenPassingNull() throws Exception {

        GenericResponse<CardBinValidationResponse> card_not_valid = GenericResponse.error(AppConstant.GEN_ERROR_CODE, "Card not valid");

        CardBinValidationRequest request = new CardBinValidationRequest();

        request.setCardNumber(null); // passing null in CardNumber

        when(validationService.validateCardBin(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), any()
        )).thenReturn(card_not_valid);

        mockMvc.perform(post("/v2/card-bin/validate")
                        .header(AppConstant.UNIT, "UNIT1")
                        .header(AppConstant.HEADER_CHANNEL, "WEB")
                        .header(AppConstant.HEADER_ACCEPT_LANGUAGE, "en")
                        .header(AppConstant.SERVICEID, "SERVICE123")
                        .header(AppConstant.SCREEN_ID, "SCREEN1")
                        .header(AppConstant.MODULE_ID, "MODULE1")
                        .header(AppConstant.SUB_MODULE_ID, "SUBMODULE1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status.code").value("000001"))
                .andExpect(jsonPath("$.status.description").value("Card number is required"));

    }


    @Test
    public void shouldReturnAllActiveBin() throws Exception {

        CardBinMaster bin1 = new CardBinMaster("401", "420374", "ISLAMIC PLATINUM", "CREDIT", "ACTIVE");
        CardBinMaster bin2 = new CardBinMaster("900", "527158", "MC PRESTIGE DEBT", "DEBIT", "ACTIVE");
        List<CardBinMaster> binList = List.of(bin1, bin2);

        GenericResponse<List<CardBinMaster>> mockResponse = GenericResponse.success(binList);
        when(validationService.getActiveBins()).thenReturn(mockResponse);

        mockMvc.perform(
                 get("/v2/card-bin/active")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].code").value("401"))
                .andExpect(jsonPath("$.data[0].bin").value("420374"))
                .andExpect(jsonPath("$.data[0].status").value("ACTIVE"))

                .andExpect(jsonPath("$.data[1].code").value("900"))
                .andExpect(jsonPath("$.data[1].bin").value("527158"))
                .andExpect(jsonPath("$.status.description").value("SUCCESS"));

    }

}
