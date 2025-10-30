## Mock Responses Documentation

### Overview
This document explains how to use and maintain mock responses for Card BIN validation. The mock service is enabled when `mock.enabled=true`.

### Mock Service
- **Class**: `com.dukhan.forgot.adapter.api.service.impl.MockCardBinValidationServiceImpl`
- **Endpoint**: `POST /validate`
- Returns `GenericResponse<SimpleValidationResponse>`

### Required Headers
- `unit`
- `channel`
- `serviceId`
- `screenId`
- `moduleId`
- `subModuleId`
- `Accept-Language` (optional)

### Request Body Schema
```json
{
  "requestInfo": {
    "cardNumber": "string(16)",
    "pin": "string(4)"
  },
  "deviceInfo": {
    "deviceId": "string",
    "ipAddress": "string",
    "vendorId": "string",
    "osVersion": "string",
    "osType": "string",
    "appVersion": "string",
    "endToEndId": "string"
  }
}
```

### Response Structure
- **Success**
```json
{
  "data": {
    "rimNumber": "...",
    "userName": "...",
    "otp": true
  },
  "status": { "code": "000000", "description": "SUCCESS" }
}
```
- **Error**
```json
{
  "data": null,
  "status": { "code": "000400", "description": "ERROR_DESCRIPTION" }
}
```

### Card Number Scenarios
- `4203741234567889` — SUCCESS → `GenericResponse_SimpleValidationResponse_Positive.json` (code `000000`)
- `3209741234567889` — BIN_NOT_VALID → `GenericResponse_SimpleValidationResponse_BinNotValid.json` (code `000400`)
- `1003741234567889` — CARD_NOT_VALID_MUST_USE_DEBIT → `GenericResponse_SimpleValidationResponse_CardNotValid.json` (code `000400`)
- `9003901234567889` — USER_BLOCKED_CONTACT_BANK → `GenericResponse_SimpleValidationResponse_UserBlocked.json` (code `000400`)
- `9898741234567889` — USER_BLOCKED_OTP_LIMIT_EXCEEDED → `GenericResponse_SimpleValidationResponse_OtpLimitExceeded.json` (code `000400`)
- `8080741234567889` — INVALID_ATTEMPTS_LIMIT_EXCEEDED → `GenericResponse_SimpleValidationResponse_InvalidAttempts.json` (code `000400`)
- `6060741234567889` — RETRY_AFTER_24_HOURS → `GenericResponse_SimpleValidationResponse_RetryAfter24Hours.json` (code `000400`)

### Files Location
```
src/main/resources/JSON/
  GenericResponse_SimpleValidationResponse.json (default)
  GenericResponse_SimpleValidationResponse_Positive.json
  GenericResponse_SimpleValidationResponse_BinNotValid.json
  GenericResponse_SimpleValidationResponse_CardNotValid.json
  GenericResponse_SimpleValidationResponse_UserBlocked.json
  GenericResponse_SimpleValidationResponse_OtpLimitExceeded.json
  GenericResponse_SimpleValidationResponse_InvalidAttempts.json
  GenericResponse_SimpleValidationResponse_RetryAfter24Hours.json
```

## Example Requests (curl)
Base URL: `http://localhost:8080`

### Positive (4203741234567889)
```bash
curl -X POST "http://localhost:8080/validate" \
  -H "Content-Type: application/json" \
  -H "unit: PRD" \
  -H "channel: MB" \
  -H "Accept-Language: en" \
  -H "serviceId: LOGIN" \
  -H "screenId: SCR001" \
  -H "moduleId: MOD001" \
  -H "subModuleId: SUB001" \
  --data-raw '{
    "requestInfo": { "cardNumber": "4203741234567889", "pin": "1234" },
    "deviceInfo": {
      "deviceId": "DEVICE123",
      "ipAddress": "192.168.1.10",
      "vendorId": "VENDOR123",
      "osVersion": "14.0",
      "osType": "iOS",
      "appVersion": "2.1.0",
      "endToEndId": "E2E-001"
    }
  }'
```

### BIN not valid (3209741234567889)
```bash
curl -X POST "http://localhost:8080/validate" \
  -H "Content-Type: application/json" \
  -H "unit: PRD" \
  -H "channel: MB" \
  -H "Accept-Language: en" \
  -H "serviceId: LOGIN" \
  -H "screenId: SCR001" \
  -H "moduleId: MOD001" \
  -H "subModuleId: SUB001" \
  --data-raw '{
    "requestInfo": { "cardNumber": "3209741234567889", "pin": "1234" },
    "deviceInfo": {
      "deviceId": "DEVICE123",
      "ipAddress": "192.168.1.10",
      "vendorId": "VENDOR123",
      "osVersion": "14.0",
      "osType": "iOS",
      "appVersion": "2.1.0",
      "endToEndId": "E2E-001"
    }
  }'
```

### Card not valid (must use debit) (1003741234567889)
```bash
curl -X POST "http://localhost:8080/validate" \
  -H "Content-Type: application/json" \
  -H "unit: PRD" \
  -H "channel: MB" \
  -H "Accept-Language: en" \
  -H "serviceId: LOGIN" \
  -H "screenId: SCR001" \
  -H "moduleId: MOD001" \
  -H "subModuleId: SUB001" \
  --data-raw '{
    "requestInfo": { "cardNumber": "1003741234567889", "pin": "1234" },
    "deviceInfo": {
      "deviceId": "DEVICE123",
      "ipAddress": "192.168.1.10",
      "vendorId": "VENDOR123",
      "osVersion": "14.0",
      "osType": "iOS",
      "appVersion": "2.1.0",
      "endToEndId": "E2E-001"
    }
  }'
```

### User blocked, contact bank (9003901234567889)
```bash
curl -X POST "http://localhost:8080/validate" \
  -H "Content-Type: application/json" \
  -H "unit: PRD" \
  -H "channel: MB" \
  -H "Accept-Language: en" \
  -H "serviceId: LOGIN" \
  -H "screenId: SCR001" \
  -H "moduleId: MOD001" \
  -H "subModuleId: SUB001" \
  --data-raw '{
    "requestInfo": { "cardNumber": "9003901234567889", "pin": "1234" },
    "deviceInfo": {
      "deviceId": "DEVICE123",
      "ipAddress": "192.168.1.10",
      "vendorId": "VENDOR123",
      "osVersion": "14.0",
      "osType": "iOS",
      "appVersion": "2.1.0",
      "endToEndId": "E2E-001"
    }
  }'
```

### User blocked (OTP limit exceeded) (9898741234567889)
```bash
curl -X POST "http://localhost:8080/validate" \
  -H "Content-Type: application/json" \
  -H "unit: PRD" \
  -H "channel: MB" \
  -H "Accept-Language: en" \
  -H "serviceId: LOGIN" \
  -H "screenId: SCR001" \
  -H "moduleId: MOD001" \
  -H "subModuleId: SUB001" \
  --data-raw '{
    "requestInfo": { "cardNumber": "9898741234567889", "pin": "1234" },
    "deviceInfo": {
      "deviceId": "DEVICE123",
      "ipAddress": "192.168.1.10",
      "vendorId": "VENDOR123",
      "osVersion": "14.0",
      "osType": "iOS",
      "appVersion": "2.1.0",
      "endToEndId": "E2E-001"
    }
  }'
```

### Invalid attempts limit exceeded (8080741234567889)
```bash
curl -X POST "http://localhost:8080/validate" \
  -H "Content-Type: application/json" \
  -H "unit: PRD" \
  -H "channel: MB" \
  -H "Accept-Language: en" \
  -H "serviceId: LOGIN" \
  -H "screenId: SCR001" \
  -H "moduleId: MOD001" \
  -H "subModuleId: SUB001" \
  --data-raw '{
    "requestInfo": { "cardNumber": "8080741234567889", "pin": "1234" },
    "deviceInfo": {
      "deviceId": "DEVICE123",
      "ipAddress": "192.168.1.10",
      "vendorId": "VENDOR123",
      "osVersion": "14.0",
      "osType": "iOS",
      "appVersion": "2.1.0",
      "endToEndId": "E2E-001"
    }
  }'
```

### Retry after 24 hours (6060741234567889)
```bash
curl -X POST "http://localhost:8080/validate" \
  -H "Content-Type: application/json" \
  -H "unit: PRD" \
  -H "channel: MB" \
  -H "Accept-Language: en" \
  -H "serviceId: LOGIN" \
  -H "screenId: SCR001" \
  -H "moduleId: MOD001" \
  -H "subModuleId: SUB001" \
  --data-raw '{
    "requestInfo": { "cardNumber": "6060741234567889", "pin": "1234" },
    "deviceInfo": {
      "deviceId": "DEVICE123",
      "ipAddress": "192.168.1.10",
      "vendorId": "VENDOR123",
      "osVersion": "14.0",
      "osType": "iOS",
      "appVersion": "2.1.0",
      "endToEndId": "E2E-001"
    }
  }'
```

## Maintenance
1. Add a new JSON file under `src/main/resources/JSON/` following the existing naming pattern.
2. Map the new card number in `getMockResponseFile()` inside `MockCardBinValidationServiceImpl`.
3. Document the scenario in this file and add a curl example.
4. Keep error codes consistent: success `000000`, errors `000400`.


