package com.dukhan.forgot.adapter.api.service.impl;


import com.dukhan.forgot.adapter.api.service.CardValidationService;
import com.dukhan.forgot.domain.model.dto.CardValidationRequest;
import com.dukhan.forgot.domain.model.dto.CardValidationResponse;
import com.dukhan.forgot.domain.model.dto.GenericResponse;
import com.dukhan.forgot.domain.model.dto.Status;
import com.dukhan.forgot.infrastructure.common.exception.BARWAHSMEncryptionException;
import com.dukhan.forgot.infrastructure.common.exception.BARWAHSMParsingException;
import com.dukhan.forgot.infrastructure.common.exception.BarwaHSMCommuicationException;
import com.dukhan.forgot.infrastructure.common.hsm.HSMEncryptorManagerImpl;
import org.springframework.stereotype.Service;

@Service
public class CardValidationServiceImpl implements CardValidationService {

    private final HSMEncryptorManagerImpl hsmEncryptor;

    public CardValidationServiceImpl(HSMEncryptorManagerImpl hsmEncryptor) {
        this.hsmEncryptor = hsmEncryptor;
    }

    @Override
    public GenericResponse<CardValidationResponse> validateCard(CardValidationRequest request) {
        try {
            String encryptedPin = hsmEncryptor.generatePinBlockUnderZPK(
                    request.getPin(),
                    request.getCardNumber()
            );
            return new GenericResponse<>(new Status("0000", "SUCCESS"), new CardValidationResponse(encryptedPin));
        } catch (BarwaHSMCommuicationException | BARWAHSMEncryptionException | BARWAHSMParsingException e) {
            return new GenericResponse<>(new Status("G-0001", e.getMessage()), null);
        } catch (Exception e) {
            return new GenericResponse<>(new Status("G-0002", "Internal Server Error"), null);
        }
    }
}
