//package com.dukhan.forgot.adapter.api.service.impl;
//
//
//
//import com.dukhan.forgot.adapter.api.service.XsdParserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.w3c.dom.Document;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import java.io.ByteArrayInputStream;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class XsdParserServiceTest {
//
//    private XsdParserService xsdParserService;
//
//    @BeforeEach
//    void setUp() {
//        xsdParserService = new XsdParserService();
//    }
//
//    @Test
//    void testParseDcardPinVerificationXsd_Success() throws Exception {
//        // Arrange: Mock a simple valid XSD in classpath (simulate by replacing resource loading)
//        String fakeXsd = """
//                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
//                           targetNamespace="urn:test:namespace"
//                           xmlns="urn:test:namespace"
//                           elementFormDefault="qualified">
//                    <xs:element name="testElement" type="xs:string" minOccurs="1"/>
//                </xs:schema>
//                """;
//
//        // Create a temporary in-memory InputStream
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(fakeXsd.getBytes());
//
//        // Manually simulate parsing (since no real classpath resource exists)
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        factory.setNamespaceAware(true);
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        Document mockDoc = builder.parse(inputStream);
//
//        // Act
//        // Instead of relying on actual classpath resource, we'll directly test methods that use Document
//        List<String> mandatoryFields = xsdParserService.getMandatoryFieldsFromXsd(mockDoc);
//        String targetNs = xsdParserService.getTargetNamespace(mockDoc);
//
//        // Assert
//        assertEquals(1, mandatoryFields.size());
//        assertEquals("testElement", mandatoryFields.get(0));
//        assertEquals("urn:test:namespace", targetNs);
//    }
//
//    @Test
//    void testGetMandatoryFieldsFromXsd_WithMultipleElements() throws Exception {
//        String multiXsd = """
//                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
//                    <xs:element name="cardNumber" type="xs:string" minOccurs="1"/>
//                    <xs:element name="pin" type="xs:string"/>
//                    <xs:element name="optionalField" type="xs:string" minOccurs="0"/>
//                </xs:schema>
//                """;
//
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(multiXsd.getBytes());
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        factory.setNamespaceAware(true);
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        Document doc = builder.parse(inputStream);
//
//        List<String> mandatoryFields = xsdParserService.getMandatoryFieldsFromXsd(doc);
//
//        assertTrue(mandatoryFields.contains("cardNumber"));
//        assertTrue(mandatoryFields.contains("pin")); // no minOccurs => default 1
//        assertFalse(mandatoryFields.contains("optionalField"));
//    }
//
//    @Test
//    void testGetTargetNamespace_WhenMissing_ReturnsDefault() throws Exception {
//        String xsd = """
//                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
//                    <xs:element name="field" type="xs:string"/>
//                </xs:schema>
//                """;
//
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(xsd.getBytes());
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        Document doc = builder.parse(inputStream);
//
//        String namespace = xsdParserService.getTargetNamespace(doc);
//        assertEquals("urn:esbbank.com/gbo/xml/schemas/v1_0/", namespace);
//    }
//
//    @Test
//    void testValidateDcardPinVerificationXsdExists_ReturnsTrueWhenPresent() {
//        boolean exists = xsdParserService.validateDcardPinVerificationXsdExists();
//        assertTrue(exists);
//    }
//
//
//    @Test
//    void testParseDcardPinVerificationXsd_FileExists_Success() throws Exception {
//        Document doc = xsdParserService.parseDcardPinVerificationXsd();
//        assertNotNull(doc);
//        assertEquals("schema", doc.getDocumentElement().getLocalName());
//    }
//
//
//    @Test
//    void testGetMandatoryFieldsFromXsd_InvalidDocument_HandlesGracefully() {
//        // Arrange: mock a document that throws NPE when used
//        Document badDoc = mock(Document.class);
//        when(badDoc.getElementsByTagNameNS(anyString(), anyString())).thenThrow(new RuntimeException("DOM Error"));
//
//        // Act
//        List<String> result = xsdParserService.getMandatoryFieldsFromXsd(badDoc);
//
//        // Assert: should return empty list, not throw
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//}
