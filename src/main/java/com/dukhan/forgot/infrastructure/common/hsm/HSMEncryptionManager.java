package com.dukhan.forgot.infrastructure.common.hsm;

public interface HSMEncryptionManager {
    String generatePinBlockUnderZPK(String clearPin, String accountNumber, String... purpose);
}

