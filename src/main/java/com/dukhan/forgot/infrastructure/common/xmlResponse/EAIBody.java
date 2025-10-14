package com.dukhan.forgot.infrastructure.common.xmlResponse;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EAIBody {
    @JacksonXmlProperty(localName = "eAI_REPLY", namespace = "urn:esbbank.com/gbo/xml/schemas/v1_0/")
    public EAIReply eaiReply;
}