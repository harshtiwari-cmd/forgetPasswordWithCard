package com.dukhan.forgot.adapter.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for parsing XSD files and extracting schema information
 */
@Service
public class XsdParserService {
    
    private static final Logger logger = LoggerFactory.getLogger(XsdParserService.class);
    
    private static final String XSD_PATH_PREFIX = "xsd/";
    private static final String XSD_EXTENSION = ".xsd";
    private static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    
    /**
     * Parse XSD file for DCARD.PIN.VERIFICATION service
     * 
     * @return Parsed XSD Document
     * @throws Exception if XSD file not found or parsing fails
     */
    public Document parseDcardPinVerificationXsd() throws Exception {
        logger.info("Parsing XSD for DCARD.PIN.VERIFICATION service");
        
        String xsdFileName = "DCARD.PIN.VERIFICATION" + XSD_EXTENSION;
        String xsdPath = XSD_PATH_PREFIX + xsdFileName;
        
        try {
            ClassPathResource resource = new ClassPathResource(xsdPath);
            if (!resource.exists()) {
                throw new IllegalArgumentException("XSD file not found: " + xsdPath);
            }
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            try (InputStream inputStream = resource.getInputStream()) {
                Document document = builder.parse(inputStream);
                logger.info("Successfully parsed XSD file: {}", xsdPath);
                return document;
            }
        } catch (Exception e) {
            logger.error("Error parsing XSD file: {} - {}", xsdPath, e.getMessage(), e);
            throw new Exception("Failed to parse XSD file: " + xsdPath, e);
        }
    }
    
    /**
     * Extract mandatory fields from XSD document
     * 
     * @param xsdDoc The parsed XSD document
     * @return List of mandatory field names
     */
    public List<String> getMandatoryFieldsFromXsd(Document xsdDoc) {
        logger.info("Extracting mandatory fields from XSD document");
        
        List<String> mandatoryFields = new ArrayList<>();
        
        try {
            // Find all elements with minOccurs="1"
            NodeList elements = xsdDoc.getElementsByTagNameNS(XSD_NAMESPACE, "element");
            
            for (int i = 0; i < elements.getLength(); i++) {
                Element element = (Element) elements.item(i);
                String minOccurs = element.getAttribute("minOccurs");
                
                // If minOccurs is "1" or not specified (default is 1)
                if ("1".equals(minOccurs) || minOccurs.isEmpty()) {
                    String elementName = element.getAttribute("name");
                    if (!elementName.isEmpty()) {
                        mandatoryFields.add(elementName);
                        logger.debug("Found mandatory field: {}", elementName);
                    }
                }
            }
            
            logger.info("Extracted {} mandatory fields from XSD", mandatoryFields.size());
        } catch (Exception e) {
            logger.error("Error extracting mandatory fields from XSD: {}", e.getMessage(), e);
        }
        
        return mandatoryFields;
    }
    
    /**
     * Get the target namespace from XSD document
     * 
     * @param xsdDoc The parsed XSD document
     * @return Target namespace URI
     */
    public String getTargetNamespace(Document xsdDoc) {
        try {
            Element schemaElement = xsdDoc.getDocumentElement();
            return schemaElement.getAttribute("targetNamespace");
        } catch (Exception e) {
            logger.error("Error getting target namespace from XSD: {}", e.getMessage(), e);
            return "urn:esbbank.com/gbo/xml/schemas/v1_0/";
        }
    }
    
    /**
     * Validate if DCARD.PIN.VERIFICATION XSD file exists
     * 
     * @return true if XSD file exists, false otherwise
     */
    public boolean validateDcardPinVerificationXsdExists() {
        String xsdFileName = "DCARD.PIN.VERIFICATION" + XSD_EXTENSION;
        String xsdPath = XSD_PATH_PREFIX + xsdFileName;
        
        try {
            ClassPathResource resource = new ClassPathResource(xsdPath);
            boolean exists = resource.exists();
            logger.debug("XSD file validation for {}: {}", xsdPath, exists ? "EXISTS" : "NOT FOUND");
            return exists;
        } catch (Exception e) {
            logger.error("Error validating XSD file existence: {} - {}", xsdPath, e.getMessage());
            return false;
        }
    }
}
