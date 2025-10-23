package com.dukhan.forgot.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpDetails {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "action", length = 50)
    private String action;

    @Column(name = "channel_id", length = 50)
    private String channelId;

    @Column(name = "confirmation_id", length = 500, nullable = false)
    private String confirmationId;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    @Column(name = "language", length = 10)
    private String language;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    @Column(name = "no_of_attempts")
    private Integer noOfAttempts;

    @Column(name = "otp_value", length = 500, nullable = false)
    private String otpValue;

    @Column(name = "rim_no", nullable = false)
    private Long rimNo;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "used_time")
    private LocalDateTime usedTime;

    @Column(name = "plain_otp", length = 10)
    private String plainOtp;

    @Column(name = "email", length = 100)
    private String email;

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
