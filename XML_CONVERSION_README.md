# XML Conversion Service Implementation

This document describes the implementation of the XSD to XML conversion service layer specifically for the DCARD.PIN.VERIFICATION service in the Spring Boot project.

## Overview

The XML conversion service provides functionality to convert JSON data to XML format based on the DCARD.PIN.VERIFICATION XSD schema. It consists of three main service classes that work together to parse the XSD file, generate XML documents, and orchestrate the conversion process. The service is integrated directly into the existing CardBinValidation endpoint.

## Service Architecture

### 1. XsdParserService
**Location**: `src/main/java/com/dukhan/forgot/adapter/api/service/XsdParserService.java`

**Responsibilities**:
- Parse XSD files from the `resources/xsd/` directory
- Extract mandatory fields (elements with `minOccurs="1"`)
- Validate XSD file existence
- Return parsed XSD documents
- Handle both folder structure and direct file access

**Key Methods**:
- `parseDcardPinVerificationXsd()`: Parse DCARD.PIN.VERIFICATION XSD file
- `getMandatoryFieldsFromXsd(Document xsdDoc)`: Extract mandatory fields
- `validateDcardPinVerificationXsdExists()`: Check if XSD file exists
- `getTargetNamespace(Document xsdDoc)`: Get target namespace from XSD

### 2. XmlGeneratorService
**Location**: `src/main/java/com/dukhan/forgot/adapter/api/service/XmlGeneratorService.java`

**Responsibilities**:
- Generate XML documents with proper namespace `urn:esbbank.com/gbo/xml/schemas/v1_0/`
- Create `eAI_MESSAGE` root element with namespace declarations
- Create `eAI_HEADER` with all header fields and security info
- Create `eAI_BODY` with `eAI_REQUEST` and service-specific request
- Handle nested objects and arrays
- Use DOM parser for XML generation
- Support UTF-8 encoding with proper formatting
- Add automatic default values (referenceNum, requestTime)

**Key Methods**:
- `generateDcardPinVerificationXml(Map<String, Object> headers, Map<String, Object> payload)`: Generate XML for DCARD.PIN.VERIFICATION service

### 3. XmlConversionService (Main Orchestrator)
**Location**: `src/main/java/com/dukhan/forgot/adapter/api/service/XmlConversionService.java`

**Responsibilities**:
- Orchestrate the conversion process
- Call XsdParserService to validate schema
- Call XmlGeneratorService to generate XML
- Handle errors and return appropriate responses
- Log all operations

**Key Methods**:
- `convertDcardPinVerificationToXml(Map<String, Object> headers, Map<String, Object> payload)`: Main conversion method for DCARD.PIN.VERIFICATION
- `getDcardPinVerificationMandatoryFields()`: Get mandatory fields for DCARD.PIN.VERIFICATION service

## Configuration

### Application Properties
The following properties have been added to `application.yml`:

```yaml
app:
  security:
    userId: ${APP_SECURITY_USER_ID:your_user_id}
    password: ${APP_SECURITY_PASSWORD:your_password}
```

### XSD Files
XSD file is located in `src/main/resources/xsd/` directory:
- `DCARD.PIN.VERIFICATION.xsd` - For debit card PIN verification

## Integration with Existing Code

### CardBinValidationServiceImpl Updates
The existing `CardBinValidationServiceImpl` has been updated to use the new XML conversion services:

1. **Dependency Injection**: Added `XmlConversionService` as a dependency
2. **XML Generation**: Replaced hardcoded XML with dynamic generation using `generateXmlRequest()` method
3. **Header Mapping**: Maps request headers to XML header elements
4. **Payload Processing**: Processes request data and converts to XML payload

### CardBinValidationResponse Updates
The `CardBinValidationResponse` DTO has been updated to include XML data:
- `generatedXmlRequest` - The generated XML request
- `mockXmlResponse` - The mock XML response for testing

## Expected XML Output Structure

The service generates XML with the following structure:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<NS1:eAI_MESSAGE xmlns:NS1="urn:esbbank.com/gbo/xml/schemas/v1_0/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="urn:esbbank.com/gbo/xml/schemas/v1_0/ ../testGen/schema/EAI.xsd">
  <NS1:eAI_HEADER>
    <NS1:serviceName>DCARD.PIN.VERIFICATION</NS1:serviceName>
    <NS1:serviceType>SYNC</NS1:serviceType>
    <NS1:serviceVersion>1</NS1:serviceVersion>
    <NS1:client>BKR</NS1:client>
    <NS1:clientChannel>MOB</NS1:clientChannel>
    <NS1:msgChannel>MQ</NS1:msgChannel>
    <NS1:requestorLanguage>E</NS1:requestorLanguage>
    <NS1:securityInfo>
      <NS1:authentication>
        <NS1:UserId>your_user_id</NS1:UserId>
        <NS1:Password>your_password</NS1:Password>
      </NS1:authentication>
      <NS1:authorization>
        <NS1:UserId>your_user_id</NS1:UserId>
      </NS1:authorization>
    </NS1:securityInfo>
    <NS1:returnCode>0000</NS1:returnCode>
  </NS1:eAI_HEADER>
  <NS1:eAI_BODY>
    <NS1:eAI_REQUEST>
      <NS1:debitCardPINVerificationRequest>
        <NS1:referenceNum>TAM650</NS1:referenceNum>
        <NS1:cardNumber>4203750000008489</NS1:cardNumber>
        <NS1:pin>07A1DDEDE9C3E500</NS1:pin>
        <NS1:requestTime>20130429233157568</NS1:requestTime>
      </NS1:debitCardPINVerificationRequest>
    </NS1:eAI_REQUEST>
  </NS1:eAI_BODY>
</NS1:eAI_MESSAGE>
```

## Service-Specific Request Element Mapping

- `DCARD.PIN.VERIFICATION` → `debitCardPINVerificationRequest`

## Usage Examples

### Using the CardBinValidation Endpoint

The XML conversion is now integrated into the existing CardBinValidation endpoint:

**Request to `POST /v2/card-bin/validate`**:
```json
{
  "cardNumber": "4203750000008489",
  "pin": "1234"
}
```

**Response includes generated XML**:
```json
{
  "status": {
    "code": "0000",
    "description": "Success"
  },
  "data": {
    "valid": true,
    "message": "Card is valid and PIN encrypted successfully",
    "bin": "42037500",
    "productType": "DEBIT",
    "cardType": "VISA",
    "code": "ACTIVE",
    "encryptedPin": "07A1DDEDE9C3E500",
    "generatedXmlRequest": "<?xml version=\"1.0\" encoding=\"UTF-8\"?>...",
    "mockXmlResponse": "<?xml version=\"1.0\" encoding=\"UTF-8\"?>..."
  }
}
```

## Error Handling

The service includes comprehensive error handling:

1. **XSD File Not Found**: Throws `IllegalArgumentException`
2. **XML Generation Errors**: Throws `Exception` with descriptive messages
3. **Validation Errors**: Returns appropriate error responses
4. **Logging**: All operations are logged with appropriate levels

## Security Features

- User credentials are injected from application properties
- Security info is automatically included in generated XML
- Both authentication and authorization sections are populated

## Future Enhancements

1. **MQ Integration**: Replace mock responses with actual MQ calls
2. **Response Parsing**: Add XML response parsing capabilities
3. **Validation**: Implement full XSD validation against generated XML
4. **Caching**: Add XSD parsing result caching for performance
5. **Multiple Namespaces**: Support for multiple namespace declarations

## Dependencies

The implementation uses the following key dependencies:
- Spring Boot Starter Web
- SLF4J for logging
- JAXP (DOM parser) for XML generation
- Spring Core for dependency injection

## Testing

The service can be tested using the provided REST endpoints or by integrating with the existing `CardBinValidationController`. All service classes are properly annotated with `@Service` and use dependency injection for testability.
