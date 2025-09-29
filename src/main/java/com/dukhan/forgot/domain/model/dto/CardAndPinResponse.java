package com.dukhan.forgot.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CardAndPinResponse {

    @JsonProperty("c")
    private String c;

    @JsonProperty("gId")
    private String gId;

    @JsonProperty("h")
    private String h;

    @JsonProperty("Key_ExportResponse")
    private String keyExportResponse;

    @JsonProperty("dfps")
    private String dfps;

    @JsonProperty("errormsg")
    private String errorMsg;

    @JsonProperty("opstatus_encryptCard")
    private int opstatusEncryptCard;

    @JsonProperty("opstatus_mob_pinAuthentication")
    private int opstatusMobPinAuthentication;

    @JsonProperty("u")
    private String u;

    @JsonProperty("errormsg1")
    private String errorMsg1;

    @JsonProperty("opstatus")
    private int opstatus;

    @JsonProperty("encNum")
    private String encNum;

    @JsonProperty("opstatus_Key_Export")
    private int opstatusKeyExport;
}
