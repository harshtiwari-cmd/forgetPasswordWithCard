package com.dukhan.forgot.infrastructure.common.hsm;


public class HSMSettings {

    private static final String SETTINGS_NAME = "HSM_SETTINGS";
    private static final String BA_CMD_RIGHT_PADDING_DIGITS_COUNT = "BA_CMD_RIGHT_PADDING_DIGITS_COUNT";

    private static HSMSettings instance;

    private HSMSettings() {
    }

    public static HSMSettings getInstance() {
        if (instance == null) {
            instance = new HSMSettings();
        }
        return instance;
    }

    public Integer getBACmdRightPaddingDigitsCount() {
        return null;
    }
}
