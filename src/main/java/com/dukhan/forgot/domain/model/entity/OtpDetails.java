package com.dukhan.forgot.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dkn_otp_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpDetails {
    
    @Id
    @Column(name = "tran_id", length = 50, nullable = false)
    private String tranId;
    
    @Id
    @Column(name = "otp_value", length = 100, nullable = false)
    private String otpValue;
    
    @Column(name = "acc_number", length = 50)
    private String accNumber;
    
    @Column(name = "encrypted_pan", length = 200)
    private String encryptedPan;
    
    @Column(name = "secure_hash", length = 200)
    private String secureHash;
    
    @Column(name = "merchantid", length = 50)
    private String merchantId;
    
    @Column(name = "acquirer_server_id", length = 50)
    private String acquirerServerId;
    
    @Column(name = "lang", length = 10)
    private String lang;
    
    @Column(name = "confirmation_id", length = 50)
    private String confirmationId;
    
    @Column(name = "otp_status", length = 20)
    private String otpStatus;
    
    @Column(name = "action", length = 50)
    private String action;
    
    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;
    
    @Column(name = "amount", length = 20)
    private String amount;
    
    @Column(name = "curr_exponent", length = 10)
    private String currExponent;
    
    @Column(name = "req_date")
    private LocalDate reqDate;
    
    @Column(name = "curr_code", length = 10)
    private String currCode;
    
    @Column(name = "merchant_country_code", length = 10)
    private String merchantCountryCode;
    
    @Column(name = "expiry_year", length = 10)
    private String expiryYear;
    
    @Column(name = "expiry_month", length = 10)
    private String expiryMonth;
    
    @Column(name = "action_ver", length = 10)
    private String actionVer;
    
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;
    
    @Column(name = "no_of_attempts")
    private Integer noOfAttempts;
    
    @Column(name = "otp_type", length = 20)
    private String otpType;
    
    @Column(name = "user_id", length = 50)
    private String userId;
    
    @Column(name = "session_id", length = 100)
    private String sessionId;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "text")
    private String userAgent;
    
    @Column(name = "used_time")
    private LocalDateTime usedTime;
    
    public static final int MAX_OTP_ATTEMPTS = 3;
    

    public boolean incrementOtpAttempts() {
        if (this.noOfAttempts == null) {
            this.noOfAttempts = 0;
        }
        
        this.noOfAttempts++;
        
        return this.noOfAttempts >= MAX_OTP_ATTEMPTS;
    }
    

    public boolean isOtpBlocked() {
        return this.noOfAttempts != null && this.noOfAttempts >= MAX_OTP_ATTEMPTS;
    }
}
