[//]: # (# Mock Responses Documentation)

[//]: # ()
[//]: # (## Overview)

[//]: # (This document provides a comprehensive guide for maintaining mock responses in the Card Bin Validation Service. The mock service is enabled when `mock.enabled=true` in the application properties.)

[//]: # ()
[//]: # (## Mock Service Implementation)

[//]: # (- **Class**: `MockCardBinValidationServiceImpl`)

[//]: # (- **Package**: `com.dukhan.forgot.adapter.api.service.impl`)

[//]: # (- **Condition**: `@ConditionalOnProperty&#40;name = "mock.enabled", havingValue = "true"&#41;`)

[//]: # ()
[//]: # (## Card Number Scenarios)

[//]: # ()
[//]: # (### 1. Positive Scenario)

[//]: # (- **Card Number**: `4203741234567889`)

[//]: # (- **Mock File**: `GenericResponse_SimpleValidationResponse_Positive.json`)

[//]: # (- **Response Code**: `000000`)

[//]: # (- **Description**: `SUCCESS`)

[//]: # (- **Data**: Returns user details with OTP enabled)

[//]: # ()
[//]: # (### 2. BIN Not Valid)

[//]: # (- **Card Number**: `3209741234567889`)

[//]: # (- **Mock File**: `GenericResponse_SimpleValidationResponse_BinNotValid.json`)

[//]: # (- **Response Code**: `100001`)

[//]: # (- **Description**: `BIN_NOT_VALID`)

[//]: # (- **Data**: `null`)

[//]: # ()
[//]: # (### 3. Card Not Valid &#40;Must Use Debit&#41;)

[//]: # (- **Card Number**: `1003741234567889`)

[//]: # (- **Mock File**: `GenericResponse_SimpleValidationResponse_CardNotValid.json`)

[//]: # (- **Response Code**: `100002`)

[//]: # (- **Description**: `CARD_NOT_VALID_MUST_USE_DEBIT`)

[//]: # (- **Data**: `null`)

[//]: # ()
[//]: # (### 4. User Blocked &#40;Contact Bank&#41;)

[//]: # (- **Card Number**: `9003901234567889`)

[//]: # (- **Mock File**: `GenericResponse_SimpleValidationResponse_UserBlocked.json`)

[//]: # (- **Response Code**: `100003`)

[//]: # (- **Description**: `USER_BLOCKED_CONTACT_BANK`)

[//]: # (- **Data**: `null`)

[//]: # ()
[//]: # (### 5. User Blocked &#40;OTP Limit Exceeded&#41;)

[//]: # (- **Card Number**: `9898741234567889`)

[//]: # (- **Mock File**: `GenericResponse_SimpleValidationResponse_OtpLimitExceeded.json`)

[//]: # (- **Response Code**: `100004`)

[//]: # (- **Description**: `USER_BLOCKED_OTP_LIMIT_EXCEEDED`)

[//]: # (- **Data**: `null`)

[//]: # ()
[//]: # (### 6. Invalid Attempts Limit Exceeded)

[//]: # (- **Card Number**: `8080741234567889`)

[//]: # (- **Mock File**: `GenericResponse_SimpleValidationResponse_InvalidAttempts.json`)

[//]: # (- **Response Code**: `100005`)

[//]: # (- **Description**: `INVALID_ATTEMPTS_LIMIT_EXCEEDED`)

[//]: # (- **Data**: `null`)

[//]: # ()
[//]: # (### 7. Retry After 24 Hours)

[//]: # (- **Card Number**: `6060741234567889`)

[//]: # (- **Mock File**: `GenericResponse_SimpleValidationResponse_RetryAfter24Hours.json`)

[//]: # (- **Response Code**: `100006`)

[//]: # (- **Description**: `RETRY_AFTER_24_HOURS`)

[//]: # (- **Data**: `null`)

[//]: # ()
[//]: # (## File Structure)

[//]: # ()
[//]: # (### Mock Response Files Location)

[//]: # (```)

[//]: # (src/main/resources/JSON/)

[//]: # (├── GenericResponse_SimpleValidationResponse.json &#40;default&#41;)

[//]: # (├── GenericResponse_SimpleValidationResponse_Positive.json)

[//]: # (├── GenericResponse_SimpleValidationResponse_BinNotValid.json)

[//]: # (├── GenericResponse_SimpleValidationResponse_CardNotValid.json)

[//]: # (├── GenericResponse_SimpleValidationResponse_UserBlocked.json)

[//]: # (├── GenericResponse_SimpleValidationResponse_OtpLimitExceeded.json)

[//]: # (├── GenericResponse_SimpleValidationResponse_InvalidAttempts.json)

[//]: # (├── GenericResponse_SimpleValidationResponse_RetryAfter24Hours.json)

[//]: # (└── GenericResponse_CardBinMasterList.json)

[//]: # (```)

[//]: # ()
[//]: # (## Adding New Mock Scenarios)

[//]: # ()
[//]: # (### Step 1: Create Mock Response File)

[//]: # (1. Create a new JSON file in `src/main/resources/JSON/`)

[//]: # (2. Follow the naming convention: `GenericResponse_SimpleValidationResponse_[ScenarioName].json`)

[//]: # (3. Use the standard response structure:)

[//]: # ()
[//]: # (```json)

[//]: # ({)

[//]: # (  "data": {)

[//]: # (    // Response data or null for error scenarios)

[//]: # (  },)

[//]: # (  "status": {)

[//]: # (    "code": "ERROR_CODE",)

[//]: # (    "description": "ERROR_DESCRIPTION")

[//]: # (  })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (### Step 2: Update Service Implementation)

[//]: # (1. Open `MockCardBinValidationServiceImpl.java`)

[//]: # (2. Add a new case in the `getMockResponseFile&#40;&#41;` method:)

[//]: # ()
[//]: # (```java)

[//]: # (case "NEW_CARD_NUMBER":)

[//]: # (    return "JSON/GenericResponse_SimpleValidationResponse_NewScenario.json";)

[//]: # (```)

[//]: # ()
[//]: # (### Step 3: Update Documentation)

[//]: # (1. Add the new scenario to this documentation)

[//]: # (2. Include card number, mock file, response codes, and descriptions)

[//]: # ()
[//]: # (## Response Structure)

[//]: # ()
[//]: # (### Success Response)

[//]: # (```json)

[//]: # ({)

[//]: # (  "data": {)

[//]: # (    "rimNumber": "card_number",)

[//]: # (    "userName": "user@example.com",)

[//]: # (    "otp": true)

[//]: # (  },)

[//]: # (  "status": {)

[//]: # (    "code": "000000",)

[//]: # (    "description": "SUCCESS")

[//]: # (  })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (### Error Response)

[//]: # (```json)

[//]: # ({)

[//]: # (  "data": null,)

[//]: # (  "status": {)

[//]: # (    "code": "ERROR_CODE",)

[//]: # (    "description": "ERROR_DESCRIPTION")

[//]: # (  })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (## Testing Mock Responses)

[//]: # ()
[//]: # (### Enable Mock Mode)

[//]: # (Set the following property in `application.properties` or `application.yml`:)

[//]: # (```properties)

[//]: # (mock.enabled=true)

[//]: # (```)

[//]: # ()
[//]: # (### Test Different Scenarios)

[//]: # (Use the following card numbers to test different scenarios:)

[//]: # ()
[//]: # (| Card Number | Expected Result |)

[//]: # (|-------------|----------------|)

[//]: # (| 4203741234567889 | Success |)

[//]: # (| 3209741234567889 | BIN Not Valid |)

[//]: # (| 1003741234567889 | Card Not Valid |)

[//]: # (| 9003901234567889 | User Blocked |)

[//]: # (| 9898741234567889 | OTP Limit Exceeded |)

[//]: # (| 8080741234567889 | Invalid Attempts |)

[//]: # (| 6060741234567889 | Retry After 24 Hours |)

[//]: # ()
[//]: # (## Maintenance Guidelines)

[//]: # ()
[//]: # (### 1. Adding New Card Numbers)

[//]: # (- Always use a unique card number pattern)

[//]: # (- Update the `getMockResponseFile&#40;&#41;` method)

[//]: # (- Create corresponding JSON response file)

[//]: # (- Update this documentation)

[//]: # ()
[//]: # (### 2. Modifying Existing Responses)

[//]: # (- Update the JSON file directly)

[//]: # (- Test the changes thoroughly)

[//]: # (- Update documentation if response structure changes)

[//]: # ()
[//]: # (### 3. Error Code Management)

[//]: # (- Use consistent error code patterns &#40;e.g., 100001, 100002, etc.&#41;)

[//]: # (- Keep error descriptions clear and user-friendly)

[//]: # (- Document all error codes in this file)

[//]: # ()
[//]: # (### 4. Testing)

[//]: # (- Always test new scenarios with actual API calls)

[//]: # (- Verify that the correct mock response is returned)

[//]: # (- Check logs to ensure proper file loading)

[//]: # ()
[//]: # (## Troubleshooting)

[//]: # ()
[//]: # (### Common Issues)

[//]: # (1. **File Not Found**: Ensure JSON files are in the correct `src/main/resources/JSON/` directory)

[//]: # (2. **JSON Parsing Error**: Validate JSON syntax in response files)

[//]: # (3. **Wrong Response**: Check card number mapping in `getMockResponseFile&#40;&#41;` method)

[//]: # (4. **Mock Not Working**: Verify `mock.enabled=true` in application properties)

[//]: # ()
[//]: # (### Debug Steps)

[//]: # (1. Check application logs for file loading errors)

[//]: # (2. Verify JSON file syntax)

[//]: # (3. Confirm card number mapping)

[//]: # (4. Test with different card numbers)

[//]: # ()
[//]: # (## Contact Information)

[//]: # (For questions or issues related to mock responses, contact the development team or refer to the main application documentation.)
