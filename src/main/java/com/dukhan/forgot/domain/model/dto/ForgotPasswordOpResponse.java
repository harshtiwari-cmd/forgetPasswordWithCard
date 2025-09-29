package com.dukhan.forgot.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ForgotPasswordOpResponse {

    @JsonProperty("opstatus")
    private int opStatus;

    @JsonProperty("message")
    private String message;

    @JsonProperty("referenceMsg")
    private String referenceMsg;

    @JsonProperty("status")
    private String status;

    @JsonProperty("url")
    private String url;
}
