//package com.dukhan.forgot.adapter.api.service.impl;
//
//import com.digi.common.constants.AppConstants;
//import com.digi.common.dto.GenericResponse;
//import com.digi.common.dto.ResultUtilVO;
//import com.digi.common.service.ReqResService;
//import com.digi.forgot.adapter.api.service.ForgotService;
//import com.digi.forgot.adapter.api.service.MiddlewareService;
//import com.digi.forgot.domain.model.dto.*;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//public class ForgotServiceImpl implements ForgotService {
//
//    @Autowired
//    private MiddlewareService middlewareService;
//
//    @Autowired
//    private ReqResService reqResService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private ResultUtilVO resultUtilVO = new ResultUtilVO();
//
//    @Autowired
//    HttpServletRequest request;
//
//    @Override
//    public GenericResponse<CardAndPinResponse> validatePin(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, CardAndPinDto cardAndPinDto) {
//        GenericResponse<CardAndPinResponse> response = middlewareService.validatePin(unit, channel, lang, serviceId, screenId, moduleId, subModuleId);
//        if(response == null) {
//            response = new GenericResponse<>();
//            response.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC));
//        }
//        return response;
//    }
//
//    @Override
//    public GenericResponse<UpdatePasswordResponse> updatepassword(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, UpdatePasswordDto updatePasswordDto) {
//        GenericResponse<UpdatePasswordResponse> response = middlewareService.updatepassword(unit, channel, lang, serviceId, screenId, moduleId, subModuleId);
//        if(response == null) {
//            response = new GenericResponse<>();
//            response.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC));
//        }
//        return response;
//    }
//
//
//    //  new API's added here
//
//    @Override
//    public GenericResponse<ForgotPasswordOpResponse> forgotPasswordOp(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, ForgotPasswordOpRequest forgotPasswordOpRequest) {
//        GenericResponse<ForgotPasswordOpResponse> response = middlewareService.forgotPasswordOp(unit, channel, lang, serviceId, screenId, moduleId, subModuleId);
//        if(response == null) {
//            response = new GenericResponse<>();
//            response.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC));
//        }
//        return response;
//    }
//
//    @Override
//    public GenericResponse<ValidateATMCardDetailsOpResponse> validateATMCardDetails(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, ValidateATMCardDetailsOpRequest validateATMCardDetailsOpRequest) {
//        GenericResponse<ValidateATMCardDetailsOpResponse> response = middlewareService.validateATMCardDetails(unit, channel, lang, serviceId, screenId, moduleId, subModuleId);
//        if(response == null) {
//            response = new GenericResponse<>();
//            response.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC));
//        }
//        return response;
//    }
//
//    @Override
//    public GenericResponse<ValidateOTPOpResponse> validateOTPOp(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, ValidateOTPOpRequest validateOTPOpRequest) {
//        GenericResponse<ValidateOTPOpResponse> response = middlewareService.validateOTPOp(unit, channel, lang, serviceId, screenId, moduleId, subModuleId);
//        if(response == null) {
//            response = new GenericResponse<>();
//            response.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC));
//        }
//        return response;
//    }
//
//
//}
