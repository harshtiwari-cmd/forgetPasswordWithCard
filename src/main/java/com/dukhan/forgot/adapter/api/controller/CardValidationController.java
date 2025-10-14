package com.dukhan.forgot.adapter.api.controller;


import com.dukhan.forgot.adapter.api.service.CardValidationService;
import com.dukhan.forgot.domain.model.dto.CardValidationRequest;
import com.dukhan.forgot.domain.model.dto.CardValidationResponse;
import com.dukhan.forgot.domain.model.dto.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/card")
public class CardValidationController {


    private final CardValidationService cardValidationService;

    public CardValidationController(CardValidationService cardValidationService) {
        this.cardValidationService = cardValidationService;
    }

    @PostMapping("/validate")
    public GenericResponse<CardValidationResponse> validateCard(@RequestBody CardValidationRequest request) {
        return cardValidationService.validateCard(request);
    }
}

