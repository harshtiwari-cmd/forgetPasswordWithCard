package com.dukhan.forgot.adapter.api.service;

import com.dukhan.forgot.domain.model.dto.BankMiddlewareRequest;
import com.dukhan.forgot.domain.model.dto.BankMiddlewareResponse;

public interface BankMiddlewareService {
    BankMiddlewareResponse callBankMiddleware(String unit, String channel, String acceptLanguage, 
                                            String serviceId, String screenId, String moduleId, 
                                            String subModuleId, BankMiddlewareRequest request);
}
