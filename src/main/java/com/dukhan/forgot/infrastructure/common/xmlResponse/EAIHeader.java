package com.dukhan.forgot.infrastructure.common.xmlResponse;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EAIHeader {
    @JacksonXmlProperty(localName = "returnCode", namespace = "urn:esbbank.com/gbo/xml/schemas/v1_0/")
    public String returnCode;
}