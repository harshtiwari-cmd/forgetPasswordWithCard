package com.dukhan.forgot.adapter.api.service;

import com.digi.common.dto.GenericResponse;
import com.digi.forgot.domain.model.dto.*;

public interface ForgotService {
    GenericResponse<CardAndPinResponse> validatePin(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, CardAndPinDto cardAndPinDto);

    GenericResponse<UpdatePasswordResponse> updatepassword(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, UpdatePasswordDto updatePasswordDto);

    //  new API's added here

    GenericResponse<ForgotPasswordOpResponse> forgotPasswordOp(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, ForgotPasswordOpRequest forgotPasswordOpRequest);

    GenericResponse<ValidateATMCardDetailsOpResponse> validateATMCardDetails(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, ValidateATMCardDetailsOpRequest validateATMCardDetailsOpRequest);

    GenericResponse<ValidateOTPOpResponse> validateOTPOp(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, ValidateOTPOpRequest validateOTPOpRequest);
}
