package com.dukhan.forgot.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {
    private String deviceId;
    private String ipAddress;
    private String vendorId;
    private String osVersion;
    private String osType;
    private String appVersion;
    private String endToEndId;
}