package com.dukhan.forgot.domain.model.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OtpGenerateResponseTest {

    @Test
    void testBuilder() {
        // Given
        OtpGenerateResponse.Status status = OtpGenerateResponse.Status.builder()
                .code("000000")
                .description("SUCCESS")
                .build();

        OtpGenerateResponse.OtpData data = OtpGenerateResponse.OtpData.builder()
                .mobileNumber("*******3335")
                .message("OTP generated successfully and sent to registered mobile number")
                .build();

        // When
        OtpGenerateResponse response = OtpGenerateResponse.builder()
                .status(status)
                .data(data)
                .build();

        // Then
        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertNotNull(response.getData());
        assertEquals("000000", response.getStatus().getCode());
        assertEquals("SUCCESS", response.getStatus().getDescription());
        assertEquals("*******3335", response.getData().getMobileNumber());
        assertEquals("OTP generated successfully and sent to registered mobile number", response.getData().getMessage());
    }

    @Test
    void testNoArgsConstructor() {
        // When
        OtpGenerateResponse response = new OtpGenerateResponse();

        // Then
        assertNotNull(response);
        assertNull(response.getStatus());
        assertNull(response.getData());
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        OtpGenerateResponse.Status status = OtpGenerateResponse.Status.builder()
                .code("000000")
                .description("SUCCESS")
                .build();

        OtpGenerateResponse.OtpData data = OtpGenerateResponse.OtpData.builder()
                .mobileNumber("*******3335")
                .message("OTP generated successfully and sent to registered mobile number")
                .build();

        // When
        OtpGenerateResponse response = new OtpGenerateResponse(status, data);

        // Then
        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertNotNull(response.getData());
        assertEquals("000000", response.getStatus().getCode());
        assertEquals("SUCCESS", response.getStatus().getDescription());
        assertEquals("*******3335", response.getData().getMobileNumber());
    }

    @Test
    void testStatusBuilder() {
        // When
        OtpGenerateResponse.Status status = OtpGenerateResponse.Status.builder()
                .code("000000")
                .description("SUCCESS")
                .build();

        // Then
        assertNotNull(status);
        assertEquals("000000", status.getCode());
        assertEquals("SUCCESS", status.getDescription());
    }

    @Test
    void testStatusNoArgsConstructor() {
        // When
        OtpGenerateResponse.Status status = new OtpGenerateResponse.Status();

        // Then
        assertNotNull(status);
        assertNull(status.getCode());
        assertNull(status.getDescription());
    }

    @Test
    void testStatusAllArgsConstructor() {
        // When
        OtpGenerateResponse.Status status = new OtpGenerateResponse.Status("000000", "SUCCESS");

        // Then
        assertNotNull(status);
        assertEquals("000000", status.getCode());
        assertEquals("SUCCESS", status.getDescription());
    }

    @Test
    void testOtpDataBuilder() {
        // When
        OtpGenerateResponse.OtpData data = OtpGenerateResponse.OtpData.builder()
                .mobileNumber("*******3335")
                .message("OTP generated successfully and sent to registered mobile number")
                .build();

        // Then
        assertNotNull(data);
        assertEquals("*******3335", data.getMobileNumber());
        assertEquals("OTP generated successfully and sent to registered mobile number", data.getMessage());
    }

    @Test
    void testOtpDataNoArgsConstructor() {
        // When
        OtpGenerateResponse.OtpData data = new OtpGenerateResponse.OtpData();

        // Then
        assertNotNull(data);
        assertNull(data.getMobileNumber());
        assertNull(data.getMessage());
    }

    @Test
    void testOtpDataAllArgsConstructor() {
        // When
        OtpGenerateResponse.OtpData data = new OtpGenerateResponse.OtpData("*******3335", "OTP generated successfully and sent to registered mobile number");

        // Then
        assertNotNull(data);
        assertEquals("*******3335", data.getMobileNumber());
        assertEquals("OTP generated successfully and sent to registered mobile number", data.getMessage());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        OtpGenerateResponse response = new OtpGenerateResponse();
        OtpGenerateResponse.Status status = new OtpGenerateResponse.Status();
        OtpGenerateResponse.OtpData data = new OtpGenerateResponse.OtpData();

        // When
        response.setStatus(status);
        response.setData(data);

        // Then
        assertEquals(status, response.getStatus());
        assertEquals(data, response.getData());
    }

    @Test
    void testStatusSettersAndGetters() {
        // Given
        OtpGenerateResponse.Status status = new OtpGenerateResponse.Status();

        // When
        status.setCode("000000");
        status.setDescription("SUCCESS");

        // Then
        assertEquals("000000", status.getCode());
        assertEquals("SUCCESS", status.getDescription());
    }

    @Test
    void testOtpDataSettersAndGetters() {
        // Given
        OtpGenerateResponse.OtpData data = new OtpGenerateResponse.OtpData();

        // When
        data.setMobileNumber("*******3335");
        data.setMessage("OTP generated successfully and sent to registered mobile number");

        // Then
        assertEquals("*******3335", data.getMobileNumber());
        assertEquals("OTP generated successfully and sent to registered mobile number", data.getMessage());
    }

    @Test
    void testToString() {
        // Given
        OtpGenerateResponse.Status status = OtpGenerateResponse.Status.builder()
                .code("000000")
                .description("SUCCESS")
                .build();

        OtpGenerateResponse.OtpData data = OtpGenerateResponse.OtpData.builder()
                .mobileNumber("*******3335")
                .message("OTP generated successfully and sent to registered mobile number")
                .build();

        OtpGenerateResponse response = OtpGenerateResponse.builder()
                .status(status)
                .data(data)
                .build();

        // When
        String toString = response.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("OtpGenerateResponse"));
    }

    @Test
    void testStatusToString() {
        // Given
        OtpGenerateResponse.Status status = OtpGenerateResponse.Status.builder()
                .code("000000")
                .description("SUCCESS")
                .build();

        // When
        String toString = status.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Status"));
    }

    @Test
    void testOtpDataToString() {
        // Given
        OtpGenerateResponse.OtpData data = OtpGenerateResponse.OtpData.builder()
                .mobileNumber("*******3335")
                .message("OTP generated successfully and sent to registered mobile number")
                .build();

        // When
        String toString = data.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("OtpData"));
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        OtpGenerateResponse.Status status1 = OtpGenerateResponse.Status.builder()
                .code("000000")
                .description("SUCCESS")
                .build();

        OtpGenerateResponse.OtpData data1 = OtpGenerateResponse.OtpData.builder()
                .mobileNumber("*******3335")
                .message("OTP generated successfully and sent to registered mobile number")
                .build();

        OtpGenerateResponse response1 = OtpGenerateResponse.builder()
                .status(status1)
                .data(data1)
                .build();

        OtpGenerateResponse response2 = OtpGenerateResponse.builder()
                .status(status1)
                .data(data1)
                .build();

        // When & Then
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testStatusEqualsAndHashCode() {
        // Given
        OtpGenerateResponse.Status status1 = OtpGenerateResponse.Status.builder()
                .code("000000")
                .description("SUCCESS")
                .build();

        OtpGenerateResponse.Status status2 = OtpGenerateResponse.Status.builder()
                .code("000000")
                .description("SUCCESS")
                .build();

        // When & Then
        assertEquals(status1, status2);
        assertEquals(status1.hashCode(), status2.hashCode());
    }

    @Test
    void testOtpDataEqualsAndHashCode() {
        // Given
        OtpGenerateResponse.OtpData data1 = OtpGenerateResponse.OtpData.builder()
                .mobileNumber("*******3335")
                .message("OTP generated successfully and sent to registered mobile number")
                .build();

        OtpGenerateResponse.OtpData data2 = OtpGenerateResponse.OtpData.builder()
                .mobileNumber("*******3335")
                .message("OTP generated successfully and sent to registered mobile number")
                .build();

        // When & Then
        assertEquals(data1, data2);
        assertEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    void testFailureResponse() {
        // Given
        OtpGenerateResponse.Status status = OtpGenerateResponse.Status.builder()
                .code("999999")
                .description("FAILED")
                .build();

        OtpGenerateResponse.OtpData data = OtpGenerateResponse.OtpData.builder()
                .message("OTP generation failed")
                .build();

        // When
        OtpGenerateResponse response = OtpGenerateResponse.builder()
                .status(status)
                .data(data)
                .build();

        // Then
        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertNotNull(response.getData());
        assertEquals("999999", response.getStatus().getCode());
        assertEquals("FAILED", response.getStatus().getDescription());
        assertEquals("OTP generation failed", response.getData().getMessage());
        assertNull(response.getData().getMobileNumber());
    }
}
