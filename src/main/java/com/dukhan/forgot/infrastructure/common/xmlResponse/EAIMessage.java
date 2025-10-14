package com.dukhan.forgot.infrastructure.common.xmlResponse;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JacksonXmlRootElement(localName = "eAI_MESSAGE", namespace = "urn:esbbank.com/gbo/xml/schemas/v1_0/")
@JsonIgnoreProperties(ignoreUnknown = true)
public class EAIMessage {
    @JacksonXmlProperty(localName = "eAI_HEADER", namespace = "urn:esbbank.com/gbo/xml/schemas/v1_0/")
    public EAIHeader eaiHeader;

    @JacksonXmlProperty(localName = "eAI_BODY", namespace = "urn:esbbank.com/gbo/xml/schemas/v1_0/")
    public EAIBody eaiBody;
}