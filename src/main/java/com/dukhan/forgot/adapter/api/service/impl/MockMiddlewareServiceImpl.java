package com.dukhan.forgot.adapter.api.service.impl;

import com.digi.common.constants.AppConstants;
import com.digi.common.dto.AppExceptionHandlerUtilDto;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.repository.JPARRmessageRepository;
import com.digi.common.repository.URLProviderRepo;
import com.digi.common.service.AsyncLogService;
import com.digi.common.util.CommonUtil;
import com.digi.common.util.DateUtil;
import com.digi.forgot.adapter.api.service.MiddlewareService;
import com.digi.forgot.domain.model.dto.*;
import com.digi.forgot.infrastructure.common.AppConstant;
import com.digi.forgot.infrastructure.helper.JsonFileReaderHelperBk;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Slf4j
@Service
@ConditionalOnProperty(name = "mock.enabled", havingValue = "true")
@RequiredArgsConstructor
public class MockMiddlewareServiceImpl implements MiddlewareService {

    private final ObjectMapper objectMapper;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private URLProviderRepo repository;

    @Autowired
    private JsonFileReaderHelperBk jsonFileReaderHelper;

    @Autowired
    private AsyncLogService service;


    ResultUtilVO resultUtilVo = new ResultUtilVO();

    @Autowired
    private JPARRmessageRepository rrMessageRepository;

    @Override
    public GenericResponse<CardAndPinResponse> validatePin(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId) {
        GenericResponse<CardAndPinResponse> response = new GenericResponse<>();

        AppExceptionHandlerUtilDto appExeDto = new AppExceptionHandlerUtilDto(unit, channel, lang, serviceId);
        appExeDto.setStartTime(DateUtil.getCurrentDate());
        try {
            appExeDto.setMicroSerId("Card and Pin Validation");
            appExeDto.setUserName(request.getHeader(AppConstant.USER_NAME));
            appExeDto.setCustomerNo(request.getHeader(AppConstants.CUSTOMER_NO));
            appExeDto.setClientSessionKey(request.getHeader("clientSessionKey"));
            appExeDto.setSessionKey(request.getHeader("sessionKey"));
            appExeDto.setModuleId(moduleId);
            appExeDto.setSubModuleId(subModuleId);
            appExeDto.setServiceId(serviceId);

            CardAndPinResponse cardAndPinResponse = jsonFileReaderHelper.readJsonFile("JSON\\CardAndPin.json", CardAndPinResponse.class);

            response.setData(cardAndPinResponse);
            response.setStatus(new ResultUtilVO("000000", "Card and Pin Validation fetched successfully"));

            String mwRequest = "{}";
            request.setAttribute("MW_REQUEST", mwRequest);
            String mwResponse = CommonUtil.getStringFromObject(cardAndPinResponse);
            request.setAttribute("MW_RESPONSE", mwResponse);
            appExeDto.setEndTime(DateUtil.getCurrentDate());

        } catch (Exception e) {
            log.error("Exception while Validating Card and Pin : ", e);
            response.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC));
        } finally {
            service.logAsync(appExeDto, rrMessageRepository, new HashMap<>(), response.getData(), response.getStatus());
        }
        return response;
    }

    @Override
    public GenericResponse<UpdatePasswordResponse> updatepassword(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId) {
        GenericResponse<UpdatePasswordResponse> response = new GenericResponse<>();

        AppExceptionHandlerUtilDto appExeDto = new AppExceptionHandlerUtilDto(unit, channel, lang, serviceId);
        appExeDto.setStartTime(DateUtil.getCurrentDate());
        try {
            appExeDto.setMicroSerId("update password");
            appExeDto.setUserName(request.getHeader(AppConstant.USER_NAME));
            appExeDto.setCustomerNo(request.getHeader(AppConstants.CUSTOMER_NO));
            appExeDto.setClientSessionKey(request.getHeader("clientSessionKey"));
            appExeDto.setSessionKey(request.getHeader("sessionKey"));
            appExeDto.setModuleId(moduleId);
            appExeDto.setSubModuleId(subModuleId);
            appExeDto.setServiceId(serviceId);

            UpdatePasswordResponse updatePasswordResponse = jsonFileReaderHelper.readJsonFile("JSON\\UpdatePassword.json", UpdatePasswordResponse.class);

            response.setData(updatePasswordResponse);
            response.setStatus(new ResultUtilVO("000000", "password updated successfully"));

            String mwRequest = "{}";
            request.setAttribute("MW_REQUEST", mwRequest);
            String mwResponse = CommonUtil.getStringFromObject(updatePasswordResponse);
            request.setAttribute("MW_RESPONSE", mwResponse);
            appExeDto.setEndTime(DateUtil.getCurrentDate());

        } catch (Exception e) {
            log.error("Exception while updating password : ", e);
            response.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC));
        } finally {
            service.logAsync(appExeDto, rrMessageRepository, new HashMap<>(), response.getData(), response.getStatus());
        }
        return response;
    }


    //  new API's added here

    @Override
    public GenericResponse<ForgotPasswordOpResponse> forgotPasswordOp(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId) {
        GenericResponse<ForgotPasswordOpResponse> response = new GenericResponse<>();

        AppExceptionHandlerUtilDto appExeDto = new AppExceptionHandlerUtilDto(unit, channel, lang, serviceId);
        appExeDto.setStartTime(DateUtil.getCurrentDate());
        try {
            appExeDto.setMicroSerId("Forgot password Op");
            appExeDto.setUserName(request.getHeader(AppConstant.USER_NAME));
            appExeDto.setCustomerNo(request.getHeader(AppConstants.CUSTOMER_NO));
            appExeDto.setClientSessionKey(request.getHeader("clientSessionKey"));
            appExeDto.setSessionKey(request.getHeader("sessionKey"));
            appExeDto.setModuleId(moduleId);
            appExeDto.setSubModuleId(subModuleId);
            appExeDto.setServiceId(serviceId);

            ForgotPasswordOpResponse forgotPasswordOpResponse = jsonFileReaderHelper.readJsonFile("JSON\\ForgotPasswordOp.json", ForgotPasswordOpResponse.class);

            response.setData(forgotPasswordOpResponse);
            response.setStatus(new ResultUtilVO("000000", "Forgot Password Op fetched successfully"));

            String mwRequest = "{}";
            request.setAttribute("MW_REQUEST", mwRequest);
            String mwResponse = CommonUtil.getStringFromObject(forgotPasswordOpResponse);
            request.setAttribute("MW_RESPONSE", mwResponse);
            appExeDto.setEndTime(DateUtil.getCurrentDate());

        } catch (Exception e) {
            log.error("Exception while getting Forgot Password Op : ", e);
            response.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC));
        } finally {
            service.logAsync(appExeDto, rrMessageRepository, new HashMap<>(), response.getData(), response.getStatus());
        }
        return response;
    }

    @Override
    public GenericResponse<ValidateATMCardDetailsOpResponse> validateATMCardDetails(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId) {
        GenericResponse<ValidateATMCardDetailsOpResponse> response = new GenericResponse<>();

        AppExceptionHandlerUtilDto appExeDto = new AppExceptionHandlerUtilDto(unit, channel, lang, serviceId);
        appExeDto.setStartTime(DateUtil.getCurrentDate());
        try {
            appExeDto.setMicroSerId("Validate ATM Card Details Op");
            appExeDto.setUserName(request.getHeader(AppConstant.USER_NAME));
            appExeDto.setCustomerNo(request.getHeader(AppConstants.CUSTOMER_NO));
            appExeDto.setClientSessionKey(request.getHeader("clientSessionKey"));
            appExeDto.setSessionKey(request.getHeader("sessionKey"));
            appExeDto.setModuleId(moduleId);
            appExeDto.setSubModuleId(subModuleId);
            appExeDto.setServiceId(serviceId);

            ValidateATMCardDetailsOpResponse validateATMCardDetailsOpResponse = jsonFileReaderHelper.readJsonFile("JSON\\ValidateATMCardDetailsOp.json", ValidateATMCardDetailsOpResponse.class);

            response.setData(validateATMCardDetailsOpResponse);
            response.setStatus(new ResultUtilVO("000000", "ATM Card Details Op Validated fetched successfully"));

            String mwRequest = "{}";
            request.setAttribute("MW_REQUEST", mwRequest);
            String mwResponse = CommonUtil.getStringFromObject(validateATMCardDetailsOpResponse);
            request.setAttribute("MW_RESPONSE", mwResponse);
            appExeDto.setEndTime(DateUtil.getCurrentDate());

        } catch (Exception e) {
            log.error("Exception while Validating ATM Card Details Op : ", e);
            response.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC));
        } finally {
            service.logAsync(appExeDto, rrMessageRepository, new HashMap<>(), response.getData(), response.getStatus());
        }
        return response;

    }

    @Override
    public GenericResponse<ValidateOTPOpResponse> validateOTPOp(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId) {
        GenericResponse<ValidateOTPOpResponse> response = new GenericResponse<>();

        AppExceptionHandlerUtilDto appExeDto = new AppExceptionHandlerUtilDto(unit, channel, lang, serviceId);
        appExeDto.setStartTime(DateUtil.getCurrentDate());
        try {
            appExeDto.setMicroSerId("Validate OTP Op Op");
            appExeDto.setUserName(request.getHeader(AppConstant.USER_NAME));
            appExeDto.setCustomerNo(request.getHeader(AppConstants.CUSTOMER_NO));
            appExeDto.setClientSessionKey(request.getHeader("clientSessionKey"));
            appExeDto.setSessionKey(request.getHeader("sessionKey"));
            appExeDto.setModuleId(moduleId);
            appExeDto.setSubModuleId(subModuleId);
            appExeDto.setServiceId(serviceId);

            ValidateOTPOpResponse validateOTPOpResponse = jsonFileReaderHelper.readJsonFile("JSON\\ValidateOTPOp.json", ValidateOTPOpResponse.class);

            response.setData(validateOTPOpResponse);
            response.setStatus(new ResultUtilVO("000000", "OTP Op Validated successfully"));

            String mwRequest = "{}";
            request.setAttribute("MW_REQUEST", mwRequest);
            String mwResponse = CommonUtil.getStringFromObject(validateOTPOpResponse);
            request.setAttribute("MW_RESPONSE", mwResponse);
            appExeDto.setEndTime(DateUtil.getCurrentDate());

        } catch (Exception e) {
            log.error("Exception while Validating Forgot Password Op : ", e);
            response.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC));
        } finally {
            service.logAsync(appExeDto, rrMessageRepository, new HashMap<>(), response.getData(), response.getStatus());
        }
        return response;

    }


}
