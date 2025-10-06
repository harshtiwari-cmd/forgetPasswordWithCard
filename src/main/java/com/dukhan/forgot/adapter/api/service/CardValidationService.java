package com.dukhan.forgot.adapter.api.service;


import com.dukhan.forgot.domain.model.dto.CardValidationRequest;
import com.dukhan.forgot.domain.model.dto.CardValidationResponse;
import com.dukhan.forgot.domain.model.dto.GenericResponse;

public interface CardValidationService {
    GenericResponse<CardValidationResponse> validateCard(CardValidationRequest request);
}

