package com.dukhan.forgot.adapter.api.service.impl;

import com.digi.common.dto.GenericResponse;
import com.digi.forgot.adapter.api.service.MiddlewareService;
import com.digi.forgot.domain.model.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "mock.enabled", havingValue = "false", matchIfMissing = true)
public class MiddlewareServiceImpl implements MiddlewareService {

    @Override
    public GenericResponse<CardAndPinResponse> validatePin(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId) {
        return null;
    }

    @Override
    public GenericResponse<UpdatePasswordResponse> updatepassword(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId) {
        return null;
    }


    //  new API's added here

    @Override
    public GenericResponse<ForgotPasswordOpResponse> forgotPasswordOp(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId) {
        return null;
    }

    @Override
    public GenericResponse<ValidateATMCardDetailsOpResponse> validateATMCardDetails(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId) {
        return null;
    }

    @Override
    public GenericResponse<ValidateOTPOpResponse> validateOTPOp(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId) {
        return null;
    }


}
