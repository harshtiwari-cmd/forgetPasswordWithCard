package com.dukhan.forgot.infrastructure.common.hsm;

import com.dukhan.forgot.infrastructure.common.MqGatewayHandler;
import com.dukhan.forgot.infrastructure.common.TCPClient;
import com.dukhan.forgot.infrastructure.common.exception.BARWAHSMEncryptionException;
import com.dukhan.forgot.infrastructure.common.exception.BARWAHSMParsingException;
import com.dukhan.forgot.infrastructure.common.exception.BarwaHSMCommuicationException;
import com.dukhan.forgot.infrastructure.settings.BarwaSettings;
import com.dukhan.forgot.infrastructure.settings.BarwaSettingsDao;
import com.dukhan.forgot.infrastructure.settings.SettingsConstants;
import com.dukhan.forgot.infrastructure.util.LoggingUtils;
import com.dukhan.forgot.infrastructure.util.OperationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HSMEncryptorManagerImpl implements HSMEncryptionManager {

    @Autowired
    private TCPClient tcpClient;
    @Autowired
    private BarwaSettingsDao barwaSettingsDao;
    private String lmkEncryptionCommandName;
    private String zpkEncryptionCommandName;
    private String zpk;

    private static int sequencer = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(HSMEncryptorManagerImpl.class);

    public String generatePinBlockUnderZPK(String clearPin, String accountNumber, String... purpose) {
        LOGGER.debug("[HSMEncryptorManagerImpl] generatePinBlockUnderZPK :Start ");
        String serviceName = purpose.length > 0 ? purpose[0] : "Registration";

        String lmkEncryptedPinBlock = generatePinBlockUnderLMK(accountNumber, clearPin, serviceName);
        LOGGER.debug("[HSMEncryptorManagerImpl] BA command :{}", lmkEncryptedPinBlock);

        String zpkEncryptedPin = translatePin(lmkEncryptedPinBlock, accountNumber, serviceName);
        LOGGER.debug("[HSMEncryptorManagerImpl] JG Command :{}", zpkEncryptedPin);

        LoggingUtils.loggingEvent(MqGatewayHandler.class, "", OperationConstants.NON_FINANCIAL_TRANSACTION,
                serviceName, "HSM : Check Coordinate", "<request><card></card></request>",
                "<reply><result></result></reply>", "0000", "SUCCESS", "", "", "");

        LOGGER.debug("[HSMEncryptorManagerImpl] generatePinBlockUnderZPK :End ");
        return zpkEncryptedPin;
    }

    public String generatePinBlockUnderLMK(String accountNumber, String clearPin, String purpose) {
        String requestRefNum = null;
        StringBuilder lmkEncryptionCommandRequest = null;
        StringBuilder lmkEncryptionCommandMaskedMessage = null;
        String lmkPinEncryptionResponse = null;
        try {
            LOGGER.debug("[HSMEncryptorManagerImpl] generatePinBlockUnderZPK :cardNumber {} ", accountNumber);
            lmkEncryptionCommandRequest = new StringBuilder();
            lmkEncryptionCommandMaskedMessage = new StringBuilder();

            requestRefNum = getReferenceNumber();
            lmkEncryptionCommandRequest.append(requestRefNum);

            lmkEncryptionCommandRequest.append(getLmkEncryptionCommandName());
            lmkEncryptionCommandMaskedMessage.append(lmkEncryptionCommandRequest);

            int numberOfRightPaddingDigits = 13;
            String rightPaddedPin = String.format("%-" + numberOfRightPaddingDigits + "s", clearPin).replace(' ', 'F');
            lmkEncryptionCommandRequest.append(rightPaddedPin);

            String trimmedCardNumber = accountNumber.substring(accountNumber.length() - 13, accountNumber.length() - 1);
            lmkEncryptionCommandRequest.append(trimmedCardNumber);
            lmkEncryptionCommandMaskedMessage.append(trimmedCardNumber);

            lmkPinEncryptionResponse = getTcpClient().executeCommand(lmkEncryptionCommandRequest.toString());

            String responseRefNum = lmkPinEncryptionResponse.substring(0, 4);
            if (responseRefNum.equals(requestRefNum)) {
                String status = lmkPinEncryptionResponse.substring(6, 8);
                if (status.equals("00")) {
                    return lmkPinEncryptionResponse.substring(8);
                } else {
                    LOGGER.debug("[(BA Command)] Error parsing HSM encryption response {} with error.code-158 ",
                            lmkPinEncryptionResponse);
                    throw new BARWAHSMParsingException("158", "Error parsing HSM encryption response");
                }
            } else {
                LOGGER.debug("[(BA Command)] Error parsing HSM encryption response {} error.code-157 ",
                        lmkPinEncryptionResponse);
                throw new BARWAHSMEncryptionException("157", "Error HSM encryption");
            }
        } catch (Exception ex) {
            LOGGER.error("[HSMEncryptorManagerImpl] Error communicating with HSM Server  exception  :", ex);
            if (ex instanceof BARWAHSMParsingException) {
                LoggingUtils.loggingEvent(MqGatewayHandler.class, requestRefNum, OperationConstants.NON_FINANCIAL_TRANSACTION,
                        purpose, " HSM :Generate pin block using lmk. (BA Command)",
                        "<request><BACommand></BACommand></request>", "<reply><result></result></reply>",
                        "FAILED", "Error parsing HSM encryption", "", "", "");
                throw new BARWAHSMParsingException("158", "Error parsing HSM encryption response");
            } else if (ex instanceof BARWAHSMEncryptionException) {
                LoggingUtils.loggingEvent(MqGatewayHandler.class, requestRefNum, OperationConstants.NON_FINANCIAL_TRANSACTION,
                        purpose, " HSM :Generate pin block using lmk. (BA Command)",
                        "<request><BACommand>" + lmkEncryptionCommandRequest.toString() + "</BACommand></request>",
                        "<reply><result></result></reply>", "FAILED", "Error HSM encryption", "", "", "");
                throw new BARWAHSMEncryptionException("157", "Error HSM encryption");
            } else {
                LoggingUtils.loggingEvent(MqGatewayHandler.class, requestRefNum, OperationConstants.NON_FINANCIAL_TRANSACTION,
                        purpose, "HSM :Generate pin block using lmk. (BA Command)",
                        "<request><BACommand>" + lmkEncryptionCommandRequest.toString() + "</BACommand></request>",
                        "<reply><result></result></reply>", "FAILED", "Error communicating with HSM Server", "", "", "");
            }
            ex.printStackTrace();
            throw new BarwaHSMCommuicationException("156", "Error communicating with HSM Server  exception");
        }
    }

    public String translatePin(String pinBlockUnderLMK, String accountNumber, String purpose) {
        String requestRefNum = null;
        String lmkPinEncryptionResponse = null;
        StringBuilder zpkEncryptionCommandRequest = null;
        try {
            zpkEncryptionCommandRequest = new StringBuilder();
            requestRefNum = getReferenceNumber();
            zpkEncryptionCommandRequest.append(requestRefNum);

            zpkEncryptionCommandRequest.append(getZpkEncryptionCommandName());
            zpkEncryptionCommandRequest.append(getZpk());
            zpkEncryptionCommandRequest.append("01");

            String trimmedCardNumber = accountNumber.substring(3, accountNumber.length() - 1);
            zpkEncryptionCommandRequest.append(trimmedCardNumber);
            zpkEncryptionCommandRequest.append(pinBlockUnderLMK);

            String lmkEncryptionCommandMsg = zpkEncryptionCommandRequest.toString();
            lmkPinEncryptionResponse = getTcpClient().executeCommand(lmkEncryptionCommandMsg);

            String responseRefNum = lmkPinEncryptionResponse.substring(0, 4);
            if (responseRefNum.equals(requestRefNum)) {
                String status = lmkPinEncryptionResponse.substring(6, 8);
                if (status.equals("00")) {
                    return lmkPinEncryptionResponse.substring(8);
                } else {
                    LOGGER.debug("[(JG command)] Error parsing HSM encryption response {} error.code-158 ",
                            lmkPinEncryptionResponse);
                    throw new BARWAHSMParsingException("158", "Error parsing HSM encryption response");
                }
            } else {
                LOGGER.debug("[(JG command)] Error parsing HSM encryption response {} error.code-157 ",
                        lmkPinEncryptionResponse);
                throw new BARWAHSMEncryptionException("157", "Error HSM encryption");
            }
        } catch (Exception ex) {
            LOGGER.error("[HSMEncryptorManagerImpl] Error communicating with HSM Server  exception  : ", ex);
            if (ex instanceof BARWAHSMParsingException) {
                LoggingUtils.loggingEvent(MqGatewayHandler.class, requestRefNum, OperationConstants.NON_FINANCIAL_TRANSACTION,
                        purpose, "HSM : Translate the pin block under lmk (JG Command)",
                        "<request><JGCommand></JGCommand></request>", "<reply><result></result></reply>",
                        "FAILED", "Error parsing HSM encryption", "", "", "");
                throw new BARWAHSMParsingException("158", "Error parsing HSM encryption response");
            } else {
                LoggingUtils.loggingEvent(MqGatewayHandler.class, requestRefNum, OperationConstants.NON_FINANCIAL_TRANSACTION,
                        purpose, "HSM : Translate the pin block under lmk (JG Command)",
                        "<request><JGCommand></JGCommand></request>", "<reply><result></result></reply>",
                        "FAILED", "Error communicating with HSM Server", "", "", "");
            }
            throw new BarwaHSMCommuicationException("156", "Error communicating with HSM Server  exception");
        }
    }

    private String getReferenceNumber() {
        if (sequencer > 9999) {
            sequencer = 1;
        }
        return String.format("%04d", sequencer++);
    }

    public TCPClient getTcpClient() {
        if (tcpClient == null) {
            LOGGER.debug("[HSMEncryptorManagerImpl] TCP is not properly configured.");
            throw new BarwaHSMCommuicationException("156", "Error communicating with HSM Server  exception");
        }
        boolean drStatus = "Y".equalsIgnoreCase(barwaSettingsDao.getSettingValue(SettingsConstants.DR_STATUS).getPropertyValue());
        BarwaSettings hsmIpSettings = barwaSettingsDao.getSettingValue(SettingsConstants.HSM_IP_ADDR);
        BarwaSettings hsmPortSettings = barwaSettingsDao.getSettingValue(SettingsConstants.HSM_PORT);
        BarwaSettings hsmTimeOutSettings = barwaSettingsDao.getSettingValue(SettingsConstants.HSM_SOCKET_READ_TIMEOUT);
        tcpClient.setSocketReadTimeout(drStatus ? Integer.parseInt(hsmTimeOutSettings.getDrValue()) : Integer.parseInt(hsmTimeOutSettings.getPropertyValue()));
        tcpClient.setPort(drStatus ? Integer.parseInt(hsmPortSettings.getDrValue()) : Integer.parseInt(hsmPortSettings.getPropertyValue()));
        tcpClient.setIpAddress(drStatus ? hsmIpSettings.getDrValue() : hsmIpSettings.getPropertyValue());
        return tcpClient;
    }

    public void setTcpClient(TCPClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    public String getLmkEncryptionCommandName() {
        return barwaSettingsDao.getSettingValue(SettingsConstants.HSM_LMK_ENCRYPTION_CMD_NAME).getPropertyValue();
    }

    public void setLmkEncryptionCommandName(String lmkEncryptionCommandName) {
        this.lmkEncryptionCommandName = lmkEncryptionCommandName;
    }

    public String getZpkEncryptionCommandName() {
        return barwaSettingsDao.getSettingValue(SettingsConstants.HSM_ZPK_ENCRYPTION_CMD_NAME).getPropertyValue();
    }

    public void setZpkEncryptionCommandName(String zpkEncryptionCommandName) {
        this.zpkEncryptionCommandName = zpkEncryptionCommandName;
    }

    public String getZpk() {
        return barwaSettingsDao.getSettingValue(SettingsConstants.HSM_ZPK).getPropertyValue();
    }

    public void setZpk(String zpk) {
        this.zpk = zpk;
    }
}
