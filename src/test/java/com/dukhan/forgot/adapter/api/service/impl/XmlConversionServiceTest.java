package com.dukhan.forgot.adapter.api.service.impl;

import com.dukhan.forgot.adapter.api.service.XmlConversionService;
import com.dukhan.forgot.adapter.api.service.XmlGeneratorService;
import com.dukhan.forgot.adapter.api.service.XsdParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.w3c.dom.Document;
import org.slf4j.Logger;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class XmlConversionServiceTest {

    @InjectMocks
    private XmlConversionService xmlConversionService;

    @Mock
    private XsdParserService xsdParserService;

    @Mock
    private XmlGeneratorService xmlGeneratorService;

    @Mock
    private Logger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void testConvertDcardPinVerificationToXml_Success() throws Exception {
        // Arrange
        Map<String, Object> headers = Map.of("serviceName", "DCARD.PIN.VERIFICATION");
        Map<String, Object> payload = Map.of("cardNumber", "123456789");

        Document mockDocument = mock(Document.class);

        when(xsdParserService.validateDcardPinVerificationXsdExists()).thenReturn(true);
        when(xsdParserService.parseDcardPinVerificationXsd()).thenReturn(mockDocument);
        when(xsdParserService.getMandatoryFieldsFromXsd(mockDocument))
                .thenReturn(List.of("cardNumber", "pin"));
        when(xmlGeneratorService.generateDcardPinVerificationXml(headers, payload))
                .thenReturn("<xml>Generated</xml>");

        // Act
        String result = xmlConversionService.convertDcardPinVerificationToXml(headers, payload);

        // Assert
        assertEquals("<xml>Generated</xml>", result);
        verify(xsdParserService).validateDcardPinVerificationXsdExists();
        verify(xsdParserService).parseDcardPinVerificationXsd();
        verify(xsdParserService).getMandatoryFieldsFromXsd(mockDocument);
        verify(xmlGeneratorService).generateDcardPinVerificationXml(headers, payload);
    }

    @Test
    void testConvertDcardPinVerificationToXml_XsdNotFound() {
        // Arrange
        Map<String, Object> headers = Map.of();
        Map<String, Object> payload = Map.of();

        when(xsdParserService.validateDcardPinVerificationXsdExists()).thenReturn(false);

        // Act + Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> xmlConversionService.convertDcardPinVerificationToXml(headers, payload));

        assertEquals("XSD file not found for DCARD.PIN.VERIFICATION service", exception.getMessage());
        verify(xsdParserService).validateDcardPinVerificationXsdExists();
    }

    @Test
    void testConvertDcardPinVerificationToXml_UnexpectedError() throws Exception {
        // Arrange
        Map<String, Object> headers = Map.of();
        Map<String, Object> payload = Map.of();

        when(xsdParserService.validateDcardPinVerificationXsdExists()).thenReturn(true);
        when(xsdParserService.parseDcardPinVerificationXsd()).thenThrow(new RuntimeException("Parsing error"));

        // Act + Assert
        Exception exception = assertThrows(Exception.class,
                () -> xmlConversionService.convertDcardPinVerificationToXml(headers, payload));

        assertTrue(exception.getMessage().contains("Failed to convert to XML"));
        verify(xsdParserService).parseDcardPinVerificationXsd();
    }



    @Test
    void testGetDcardPinVerificationMandatoryFields_Success() throws Exception {
        // Arrange
        Document mockDocument = mock(Document.class);
        List<String> expectedFields = List.of("cardNumber", "pin");

        when(xsdParserService.parseDcardPinVerificationXsd()).thenReturn(mockDocument);
        when(xsdParserService.getMandatoryFieldsFromXsd(mockDocument)).thenReturn(expectedFields);

        // Act
        List<String> result = xmlConversionService.getDcardPinVerificationMandatoryFields();

        // Assert
        assertEquals(expectedFields, result);
        verify(xsdParserService).parseDcardPinVerificationXsd();
        verify(xsdParserService).getMandatoryFieldsFromXsd(mockDocument);
    }

    @Test
    void testGetDcardPinVerificationMandatoryFields_Failure() throws Exception {

        when(xsdParserService.parseDcardPinVerificationXsd()).thenThrow(new RuntimeException("XSD parse failed"));


        Exception exception = assertThrows(Exception.class,
                () -> xmlConversionService.getDcardPinVerificationMandatoryFields());

        assertTrue(exception.getMessage().contains("Failed to get mandatory fields"));
        verify(xsdParserService).parseDcardPinVerificationXsd();
    }
}
