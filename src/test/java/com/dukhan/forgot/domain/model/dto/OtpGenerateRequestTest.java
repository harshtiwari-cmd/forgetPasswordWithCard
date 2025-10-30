package com.dukhan.forgot.domain.model.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OtpGenerateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testBuilder() {
        OtpGenerateRequest.RequestInfo requestInfo = OtpGenerateRequest.RequestInfo.builder()
                .action("login")
                .customerId("+97433333335")
                .build();

        DeviceInfo deviceInfo = DeviceInfo.builder()
                .deviceId("DEVICE123")
                .ipAddress("192.168.1.1")
                .vendorId("VENDOR123")
                .osVersion("1.0.0")
                .osType("Android")
                .appVersion("2.1.0")
                .endToEndId("E2E123")
                .build();

        OtpGenerateRequest request = OtpGenerateRequest.builder()
                .requestInfo(requestInfo)
                .deviceInfo(deviceInfo)
                .build();

        assertNotNull(request);
        assertNotNull(request.getRequestInfo());
        assertNotNull(request.getDeviceInfo());
        assertEquals("login", request.getRequestInfo().getAction());
        assertEquals("+97433333335", request.getRequestInfo().getCustomerId());
        assertEquals("DEVICE123", request.getDeviceInfo().getDeviceId());
        assertEquals("192.168.1.1", request.getDeviceInfo().getIpAddress());
    }

    @Test
    void testNoArgsConstructor() {
        OtpGenerateRequest request = new OtpGenerateRequest();

        assertNotNull(request);
        assertNull(request.getRequestInfo());
        assertNull(request.getDeviceInfo());
    }

    @Test
    void testAllArgsConstructor() {
        OtpGenerateRequest.RequestInfo requestInfo = OtpGenerateRequest.RequestInfo.builder()
                .action("login")
                .customerId("+97433333335")
                .build();

        DeviceInfo deviceInfo = DeviceInfo.builder()
                .deviceId("DEVICE123")
                .ipAddress("192.168.1.1")
                .vendorId("VENDOR123")
                .osVersion("1.0.0")
                .osType("Android")
                .appVersion("2.1.0")
                .endToEndId("E2E123")
                .build();

        OtpGenerateRequest request = new OtpGenerateRequest(requestInfo, deviceInfo);

        assertNotNull(request);
        assertNotNull(request.getRequestInfo());
        assertNotNull(request.getDeviceInfo());
        assertEquals("login", request.getRequestInfo().getAction());
        assertEquals("+97433333335", request.getRequestInfo().getCustomerId());
        assertEquals("DEVICE123", request.getDeviceInfo().getDeviceId());
    }

    @Test
    void testRequestInfoBuilder() {
        OtpGenerateRequest.RequestInfo requestInfo = OtpGenerateRequest.RequestInfo.builder()
                .action("login")
                .customerId("+97433333335")
                .build();

        assertNotNull(requestInfo);
        assertEquals("login", requestInfo.getAction());
        assertEquals("+97433333335", requestInfo.getCustomerId());
    }

    @Test
    void testRequestInfoNoArgsConstructor() {
        OtpGenerateRequest.RequestInfo requestInfo = new OtpGenerateRequest.RequestInfo();

        assertNotNull(requestInfo);
        assertNull(requestInfo.getAction());
        assertNull(requestInfo.getCustomerId());
    }

    @Test
    void testRequestInfoAllArgsConstructor() {
        OtpGenerateRequest.RequestInfo requestInfo = new OtpGenerateRequest.RequestInfo("login", "+97433333335");

        assertNotNull(requestInfo);
        assertEquals("login", requestInfo.getAction());
        assertEquals("+97433333335", requestInfo.getCustomerId());
    }

    @Test
    void testValidation_ValidRequest() {
        OtpGenerateRequest.RequestInfo requestInfo = OtpGenerateRequest.RequestInfo.builder()
                .action("login")
                .customerId("+97433333335")
                .build();

        DeviceInfo deviceInfo = DeviceInfo.builder()
                .deviceId("DEVICE123")
                .ipAddress("192.168.1.1")
                .vendorId("VENDOR123")
                .osVersion("1.0.0")
                .osType("Android")
                .appVersion("2.1.0")
                .endToEndId("E2E123")
                .build();

        OtpGenerateRequest request = OtpGenerateRequest.builder()
                .requestInfo(requestInfo)
                .deviceInfo(deviceInfo)
                .build();

        Set<ConstraintViolation<OtpGenerateRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_NullRequestInfo() {
        DeviceInfo deviceInfo = DeviceInfo.builder()
                .deviceId("DEVICE123")
                .ipAddress("192.168.1.1")
                .vendorId("VENDOR123")
                .osVersion("1.0.0")
                .osType("Android")
                .appVersion("2.1.0")
                .endToEndId("E2E123")
                .build();

        OtpGenerateRequest request = OtpGenerateRequest.builder()
                .requestInfo(null)
                .deviceInfo(deviceInfo)
                .build();

        Set<ConstraintViolation<OtpGenerateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Request info is required")));
    }

    @Test
    void testValidation_NullDeviceInfo() {
        OtpGenerateRequest.RequestInfo requestInfo = OtpGenerateRequest.RequestInfo.builder()
                .action("login")
                .customerId("+97433333335")
                .build();

        OtpGenerateRequest request = OtpGenerateRequest.builder()
                .requestInfo(requestInfo)
                .deviceInfo(null)
                .build();

        Set<ConstraintViolation<OtpGenerateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Device info is required")));
    }

    @Test
    void testValidation_RequestInfoWithBlankAction() {
        OtpGenerateRequest.RequestInfo requestInfo = OtpGenerateRequest.RequestInfo.builder()
                .action("")
                .customerId("+97433333335")
                .build();

        DeviceInfo deviceInfo = DeviceInfo.builder()
                .deviceId("DEVICE123")
                .ipAddress("192.168.1.1")
                .vendorId("VENDOR123")
                .osVersion("1.0.0")
                .osType("Android")
                .appVersion("2.1.0")
                .endToEndId("E2E123")
                .build();

        OtpGenerateRequest request = OtpGenerateRequest.builder()
                .requestInfo(requestInfo)
                .deviceInfo(deviceInfo)
                .build();

        Set<ConstraintViolation<OtpGenerateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Action is required")));
    }

    @Test
    void testValidation_RequestInfoWithBlankMobileNumber() {
        OtpGenerateRequest.RequestInfo requestInfo = OtpGenerateRequest.RequestInfo.builder()
                .action("login")
                .customerId("")
                .build();

        DeviceInfo deviceInfo = DeviceInfo.builder()
                .deviceId("DEVICE123")
                .ipAddress("192.168.1.1")
                .vendorId("VENDOR123")
                .osVersion("1.0.0")
                .osType("Android")
                .appVersion("2.1.0")
                .endToEndId("E2E123")
                .build();

        OtpGenerateRequest request = OtpGenerateRequest.builder()
                .requestInfo(requestInfo)
                .deviceInfo(deviceInfo)
                .build();

        Set<ConstraintViolation<OtpGenerateRequest>> violations = validator.validate(request);

        System.out.println(violations);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Customer number is required")));
    }

    @Test
    void testSettersAndGetters() {
        OtpGenerateRequest request = new OtpGenerateRequest();
        OtpGenerateRequest.RequestInfo requestInfo = new OtpGenerateRequest.RequestInfo();
        DeviceInfo deviceInfo = new DeviceInfo();

        request.setRequestInfo(requestInfo);
        request.setDeviceInfo(deviceInfo);

        assertEquals(requestInfo, request.getRequestInfo());
        assertEquals(deviceInfo, request.getDeviceInfo());
    }

    @Test
    void testToString() {
        OtpGenerateRequest.RequestInfo requestInfo = OtpGenerateRequest.RequestInfo.builder()
                .action("login")
                .customerId("+97433333335")
                .build();

        DeviceInfo deviceInfo = DeviceInfo.builder()
                .deviceId("DEVICE123")
                .ipAddress("192.168.1.1")
                .vendorId("VENDOR123")
                .osVersion("1.0.0")
                .osType("Android")
                .appVersion("2.1.0")
                .endToEndId("E2E123")
                .build();

        OtpGenerateRequest request = OtpGenerateRequest.builder()
                .requestInfo(requestInfo)
                .deviceInfo(deviceInfo)
                .build();

        // When
        String toString = request.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("OtpGenerateRequest"));
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        OtpGenerateRequest.RequestInfo requestInfo1 = OtpGenerateRequest.RequestInfo.builder()
                .action("login")
                .customerId("+97433333335")
                .build();

        DeviceInfo deviceInfo1 = DeviceInfo.builder()
                .deviceId("DEVICE123")
                .ipAddress("192.168.1.1")
                .vendorId("VENDOR123")
                .osVersion("1.0.0")
                .osType("Android")
                .appVersion("2.1.0")
                .endToEndId("E2E123")
                .build();

        OtpGenerateRequest request1 = OtpGenerateRequest.builder()
                .requestInfo(requestInfo1)
                .deviceInfo(deviceInfo1)
                .build();

        OtpGenerateRequest request2 = OtpGenerateRequest.builder()
                .requestInfo(requestInfo1)
                .deviceInfo(deviceInfo1)
                .build();

        // When & Then
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }
}
