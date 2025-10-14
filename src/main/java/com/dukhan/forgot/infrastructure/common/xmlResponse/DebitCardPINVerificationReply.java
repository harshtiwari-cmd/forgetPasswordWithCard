package com.dukhan.forgot.infrastructure.common.xmlResponse;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebitCardPINVerificationReply {
    @JacksonXmlProperty(localName = "referenceNum", namespace = "urn:esbbank.com/gbo/xml/schemas/v1_0/")
    public String referenceNum;
    @JacksonXmlProperty(localName = "requestTime", namespace = "urn:esbbank.com/gbo/xml/schemas/v1_0/")
    public String requestTime;
    @JacksonXmlProperty(localName = "returnStatus", namespace = "urn:esbbank.com/gbo/xml/schemas/v1_0/")
    public ReturnStatus returnStatus;
    @JacksonXmlProperty(localName = "returnStatusProvider", namespace = "urn:esbbank.com/gbo/xml/schemas/v1_0/")
    public ReturnStatusProvider returnStatusProvider;
}