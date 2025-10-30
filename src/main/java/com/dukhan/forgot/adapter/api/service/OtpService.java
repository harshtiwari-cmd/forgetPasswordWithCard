package com.dukhan.forgot.adapter.api.service;

import com.dukhan.forgot.domain.model.dto.OtpGenerateRequest;
import com.dukhan.forgot.domain.model.dto.OtpGenerateResponse;

public interface OtpService {
    

    OtpGenerateResponse generateOtp(String unit, String channel, String lang, String serviceId, 
                                   String screenId, String moduleId, String subModuleId, 
                                   OtpGenerateRequest request);
}
