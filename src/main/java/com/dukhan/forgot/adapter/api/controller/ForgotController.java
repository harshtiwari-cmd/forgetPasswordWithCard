//package com.dukhan.forgot.adapter.api.controller;
//
//import com.digi.common.constants.AppConstants;
//import com.digi.common.dto.GenericResponse;
//import com.digi.forgot.adapter.api.service.ForgotService;
//import com.digi.forgot.domain.model.dto.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/v2/forgot")
//public class ForgotController {
//
//    @Autowired
//    private ForgotService forgotService;
//
//    @PostMapping("/card/validation")
//    public GenericResponse<CardAndPinResponse> validatePin(@RequestHeader(name = AppConstants.UNIT) String unit,
//                                                           @RequestHeader(name = AppConstants.CHANNEL) String channel,
//                                                           @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE) String lang,
//                                                           @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
//                                                           @RequestHeader(name = AppConstants.SCREENID) String screenId,
//                                                           @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
//                                                           @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
//                                                           @RequestBody CardAndPinDto cardAndPinDto) {
//
//        return forgotService.validatePin(unit,channel,lang,serviceId,screenId,moduleId,subModuleId,cardAndPinDto);
//    }
//
//    @PostMapping("/update/password")
//    public GenericResponse<UpdatePasswordResponse> updatepassword(@RequestHeader(name = AppConstants.UNIT) String unit,
//                                                                  @RequestHeader(name = AppConstants.CHANNEL) String channel,
//                                                                  @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE) String lang,
//                                                                  @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
//                                                                  @RequestHeader(name = AppConstants.SCREENID) String screenId,
//                                                                  @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
//                                                                  @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
//                                                                  @RequestBody UpdatePasswordDto updatePasswordDto) {
//
//        return forgotService.updatepassword(unit,channel,lang,serviceId,screenId,moduleId,subModuleId,updatePasswordDto);
//    }
//
//    //  new API's added here
//
//    @PostMapping("/password/op")
//    public GenericResponse<ForgotPasswordOpResponse> forgotPasswordOp(@RequestHeader(name = AppConstants.UNIT) String unit,
//                                                                    @RequestHeader(name = AppConstants.CHANNEL) String channel,
//                                                                    @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE) String lang,
//                                                                    @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
//                                                                    @RequestHeader(name = AppConstants.SCREENID) String screenId,
//                                                                    @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
//                                                                    @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
//                                                                    @RequestBody ForgotPasswordOpRequest forgotPasswordOpRequest) {
//
//        return forgotService.forgotPasswordOp(unit,channel,lang,serviceId,screenId,moduleId,subModuleId,forgotPasswordOpRequest);
//    }
//
//    @PostMapping("/validate/atm/card/details/op")
//    public GenericResponse<ValidateATMCardDetailsOpResponse> validateATMCardDetails(@RequestHeader(name = AppConstants.UNIT) String unit,
//                                                                      @RequestHeader(name = AppConstants.CHANNEL) String channel,
//                                                                      @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE) String lang,
//                                                                      @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
//                                                                      @RequestHeader(name = AppConstants.SCREENID) String screenId,
//                                                                      @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
//                                                                      @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
//                                                                      @RequestBody ValidateATMCardDetailsOpRequest validateATMCardDetailsOpRequest) {
//
//        return forgotService.validateATMCardDetails(unit,channel,lang,serviceId,screenId,moduleId,subModuleId,validateATMCardDetailsOpRequest);
//    }
//
//    @PostMapping("/validate/otp/op")
//    public GenericResponse<ValidateOTPOpResponse> validateOTPOp(@RequestHeader(name = AppConstants.UNIT) String unit,
//                                                                      @RequestHeader(name = AppConstants.CHANNEL) String channel,
//                                                                      @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE) String lang,
//                                                                      @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
//                                                                      @RequestHeader(name = AppConstants.SCREENID) String screenId,
//                                                                      @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
//                                                                      @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
//                                                                      @RequestBody ValidateOTPOpRequest validateOTPOpRequest) {
//
//        return forgotService.validateOTPOp(unit,channel,lang,serviceId,screenId,moduleId,subModuleId,validateOTPOpRequest);
//    }
//}
