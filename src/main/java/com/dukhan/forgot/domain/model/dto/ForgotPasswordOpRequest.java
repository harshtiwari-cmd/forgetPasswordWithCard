package com.dukhan.forgot.domain.model.dto;

import lombok.Data;

@Data
public class ForgotPasswordOpRequest {

    private String userName;
    private String sessionUserId;
    private String cCode;
    private String branchCode;
    private String customerNum;
    private String userId;
    private String cardMonth;
    private String cardNumber;
    private String cardPin;
    private String cardYear;
    private String countryCode;
}
