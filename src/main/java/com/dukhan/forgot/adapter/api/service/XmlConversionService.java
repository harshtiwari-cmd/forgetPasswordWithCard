package com.dukhan.forgot.adapter.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Map;

/**
 * Main service for orchestrating XSD to XML conversion process
 */
@Service
public class XmlConversionService {
    
    private static final Logger logger = LoggerFactory.getLogger(XmlConversionService.class);
    
    @Autowired
    private XsdParserService xsdParserService;
    
    @Autowired
    private XmlGeneratorService xmlGeneratorService;
    
    /**
     * Convert JSON data to XML for DCARD.PIN.VERIFICATION service
     * 
     * @param headers Map containing header information
     * @param payload Map containing payload data
     * @return Generated XML string
     * @throws Exception if conversion fails
     */
    public String convertDcardPinVerificationToXml(Map<String, Object> headers, Map<String, Object> payload) throws Exception {
        logger.info("Starting XML conversion for DCARD.PIN.VERIFICATION service");
        
        try {
            if (!xsdParserService.validateDcardPinVerificationXsdExists()) {
                throw new IllegalArgumentException("XSD file not found for DCARD.PIN.VERIFICATION service");
            }
            
            Document xsdDocument = xsdParserService.parseDcardPinVerificationXsd();
            logger.debug("Successfully parsed XSD for DCARD.PIN.VERIFICATION service");
            
            List<String> mandatoryFields = xsdParserService.getMandatoryFieldsFromXsd(xsdDocument);
            logger.debug("Found {} mandatory fields in XSD for DCARD.PIN.VERIFICATION service", mandatoryFields.size());
            
            String generatedXml = xmlGeneratorService.generateDcardPinVerificationXml(headers, payload);
            
            logger.info("Successfully completed XML conversion for DCARD.PIN.VERIFICATION service");
            return generatedXml;
            
        } catch (IllegalArgumentException e) {
            logger.error("Validation error during XML conversion for DCARD.PIN.VERIFICATION service: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error during XML conversion for DCARD.PIN.VERIFICATION service: {}", e.getMessage(), e);
            throw new Exception("Failed to convert to XML for DCARD.PIN.VERIFICATION service", e);
        }
    }
    
    /**
     * Get list of mandatory fields for DCARD.PIN.VERIFICATION service
     * 
     * @return List of mandatory field names
     * @throws Exception if XSD parsing fails
     */
    public List<String> getDcardPinVerificationMandatoryFields() throws Exception {
        logger.info("Getting mandatory fields for DCARD.PIN.VERIFICATION service");
        
        try {
            Document xsdDocument = xsdParserService.parseDcardPinVerificationXsd();
            return xsdParserService.getMandatoryFieldsFromXsd(xsdDocument);
        } catch (Exception e) {
            logger.error("Error getting mandatory fields for DCARD.PIN.VERIFICATION service: {}", e.getMessage(), e);
            throw new Exception("Failed to get mandatory fields for DCARD.PIN.VERIFICATION service", e);
        }
    }
}
