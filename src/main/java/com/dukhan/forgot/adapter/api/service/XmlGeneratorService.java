package com.dukhan.forgot.adapter.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Service for generating XML documents from JSON data based on XSD schemas
 */
@Service
public class XmlGeneratorService {
    
    private static final Logger logger = LoggerFactory.getLogger(XmlGeneratorService.class);
    
    private static final String TARGET_NAMESPACE = "urn:esbbank.com/gbo/xml/schemas/v1_0/";
    private static final String NS1_PREFIX = "NS1";
    private static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XSI_PREFIX = "xsi";
    
    @Value("${app.security.userId}")
    private String userId;
    
    @Value("${app.security.password}")
    private String password;
    
    /**
     * Generate XML for DCARD.PIN.VERIFICATION service
     * 
     * @param headers Map containing header information
     * @param payload Map containing payload data
     * @return Generated XML string
     * @throws Exception if XML generation fails
     */
    public String generateDcardPinVerificationXml(Map<String, Object> headers, Map<String, Object> payload) throws Exception {
        logger.info("Generating XML for DCARD.PIN.VERIFICATION service");
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            
            // Create root element with namespaces
            Element rootElement = createRootElement(document);
            document.appendChild(rootElement);
            
            // Create eAI_HEADER
            Element headerElement = createHeaderElement(document, headers);
            rootElement.appendChild(headerElement);
            
            // Create eAI_BODY
            Element bodyElement = createBodyElement(document, payload);
            rootElement.appendChild(bodyElement);
            
            // Convert to string
            String xmlString = documentToString(document);
            logger.info("Successfully generated XML for DCARD.PIN.VERIFICATION service");
            return xmlString;
            
        } catch (Exception e) {
            logger.error("Error generating XML for DCARD.PIN.VERIFICATION service: {}", e.getMessage(), e);
            throw new Exception("Failed to generate XML for DCARD.PIN.VERIFICATION service", e);
        }
    }
    
    /**
     * Create root element with namespace declarations
     */
    private Element createRootElement(Document document) {
        Element rootElement = document.createElementNS(TARGET_NAMESPACE, NS1_PREFIX + ":eAI_MESSAGE");
        
        // Add namespace declarations
        rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + NS1_PREFIX, TARGET_NAMESPACE);
        rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + XSI_PREFIX, XSI_NAMESPACE);
        rootElement.setAttributeNS(XSI_NAMESPACE, XSI_PREFIX + ":schemaLocation", 
            TARGET_NAMESPACE + " ../testGen/schema/EAI.xsd");
        
        return rootElement;
    }
    
    /**
     * Create eAI_HEADER element with all header fields
     */
    private Element createHeaderElement(Document document, Map<String, Object> headers) {
        Element headerElement = document.createElementNS(TARGET_NAMESPACE, NS1_PREFIX + ":eAI_HEADER");
        
        // Service name
        Element serviceNameElement = createTextElement(document, "serviceName", 
            getStringValue(headers, "serviceName", "DCARD.PIN.VERIFICATION"));
        headerElement.appendChild(serviceNameElement);
        
        // Service type
        Element serviceTypeElement = createTextElement(document, "serviceType", 
            getStringValue(headers, "serviceType", "SYNC"));
        headerElement.appendChild(serviceTypeElement);
        
        // Service version
        Element serviceVersionElement = createTextElement(document, "serviceVersion", 
            getStringValue(headers, "serviceVersion", "1"));
        headerElement.appendChild(serviceVersionElement);
        
        // Client
        Element clientElement = createTextElement(document, "client", 
            getStringValue(headers, "client", "BKR"));
        headerElement.appendChild(clientElement);
        
        // Client channel
        Element clientChannelElement = createTextElement(document, "clientChannel", 
            getStringValue(headers, "clientChannel", "MOB"));
        headerElement.appendChild(clientChannelElement);
        
        // Message channel
        Element msgChannelElement = createTextElement(document, "msgChannel", 
            getStringValue(headers, "msgChannel", "MQ"));
        headerElement.appendChild(msgChannelElement);
        
        // Requestor language
        Element requestorLanguageElement = createTextElement(document, "requestorLanguage", 
            getStringValue(headers, "requestorLanguage", "E"));
        headerElement.appendChild(requestorLanguageElement);
        
        // Security info
        Element securityInfoElement = createSecurityInfoElement(document);
        headerElement.appendChild(securityInfoElement);
        
        // Return code
        Element returnCodeElement = createTextElement(document, "returnCode", 
            getStringValue(headers, "returnCode", "0000"));
        headerElement.appendChild(returnCodeElement);
        
        return headerElement;
    }
    
    /**
     * Create security info element with authentication and authorization
     */
    private Element createSecurityInfoElement(Document document) {
        Element securityInfoElement = document.createElementNS(TARGET_NAMESPACE, NS1_PREFIX + ":securityInfo");
        
        // Authentication
        Element authenticationElement = document.createElementNS(TARGET_NAMESPACE, NS1_PREFIX + ":authentication");
        
        Element userIdAuthElement = createTextElement(document, "UserId", userId);
        authenticationElement.appendChild(userIdAuthElement);
        
        Element passwordElement = createTextElement(document, "Password", password);
        authenticationElement.appendChild(passwordElement);
        
        securityInfoElement.appendChild(authenticationElement);
        
        // Authorization
        Element authorizationElement = document.createElementNS(TARGET_NAMESPACE, NS1_PREFIX + ":authorization");
        
        Element userIdAuthzElement = createTextElement(document, "UserId", userId);
        authorizationElement.appendChild(userIdAuthzElement);
        
        securityInfoElement.appendChild(authorizationElement);
        
        return securityInfoElement;
    }
    
    /**
     * Create eAI_BODY element with DCARD.PIN.VERIFICATION request
     */
    private Element createBodyElement(Document document, Map<String, Object> payload) {
        Element bodyElement = document.createElementNS(TARGET_NAMESPACE, NS1_PREFIX + ":eAI_BODY");
        
        // eAI_REQUEST
        Element requestElement = document.createElementNS(TARGET_NAMESPACE, NS1_PREFIX + ":eAI_REQUEST");
        
        // DCARD.PIN.VERIFICATION request element
        Element serviceRequestElement = createDcardPinVerificationRequestElement(document, payload);
        requestElement.appendChild(serviceRequestElement);
        
        bodyElement.appendChild(requestElement);
        
        return bodyElement;
    }
    
    /**
     * Create DCARD.PIN.VERIFICATION request element
     */
    private Element createDcardPinVerificationRequestElement(Document document, Map<String, Object> payload) {
        Element serviceRequestElement = document.createElementNS(TARGET_NAMESPACE, NS1_PREFIX + ":debitCardPINVerificationRequest");
        
        // Add payload data to the request element
        if (payload != null) {
            for (Map.Entry<String, Object> entry : payload.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                
                // Handle special cases
                if ("requestTime".equals(key) && (value == null || value.toString().isEmpty())) {
                    value = generateRequestTime();
                }
                if ("referenceNum".equals(key) && (value == null || value.toString().isEmpty())) {
                    value = generateReferenceNum();
                }
                
                Element fieldElement = createTextElement(document, key, value.toString());
                serviceRequestElement.appendChild(fieldElement);
            }
        }
        
        return serviceRequestElement;
    }
    
    /**
     * Create a text element with namespace
     */
    private Element createTextElement(Document document, String elementName, String textContent) {
        Element element = document.createElementNS(TARGET_NAMESPACE, NS1_PREFIX + ":" + elementName);
        element.setTextContent(textContent);
        return element;
    }
    
    /**
     * Get string value from map with default fallback
     */
    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        if (map != null && map.containsKey(key) && map.get(key) != null) {
            return map.get(key).toString();
        }
        return defaultValue;
    }
    
    /**
     * Generate request time in the required format
     */
    private String generateRequestTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }
    
    /**
     * Generate reference number
     */
    private String generateReferenceNum() {
        return "TAM" + System.currentTimeMillis() % 1000000;
    }
    
    /**
     * Convert Document to formatted XML string
     */
    private String documentToString(Document document) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        
        // Set output properties for formatting
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        
        return writer.toString();
    }
}
