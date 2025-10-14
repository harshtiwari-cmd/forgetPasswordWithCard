package com.dukhan.forgot.infrastructure.settings;


import org.springframework.stereotype.Repository;

@Repository
public class BarwaSettingsDao {
    public BarwaSettings getSettingValue(String key) {
        BarwaSettings setting = new BarwaSettings();
        switch (key) {
            case "HSM_LMK_ENCRYPTION_CMD_NAME": setting.setPropertyValue("BA"); break;
            case "HSM_ZPK_ENCRYPTION_CMD_NAME": setting.setPropertyValue("JG"); break;
            case "HSM_ZPK": setting.setPropertyValue("ZPKVALUE123"); break;
            case "DR_STATUS": setting.setPropertyValue("N"); break;
            case "HSM_IP_ADDR": setting.setPropertyValue("127.0.0.1"); break;
            case "HSM_PORT": setting.setPropertyValue("12345"); break;
            case "HSM_SOCKET_READ_TIMEOUT": setting.setPropertyValue("5000"); break;
            default: setting.setPropertyValue(""); break;
        }
        return setting;
    }
}
