package com.dukhan.forgot.adapter.api.service.impl;

import com.dukhan.forgot.domain.model.dto.BankMiddlewareRequest;
import com.dukhan.forgot.domain.model.dto.BankMiddlewareResponse;
import com.dukhan.forgot.domain.model.dto.BankMiddlewareResponse.BankResponse;
import com.dukhan.forgot.domain.model.dto.BankMiddlewareResponse.ReturnStatus;
import com.dukhan.forgot.domain.model.dto.BankMiddlewareResponse.ReturnStatusProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankMiddlewareServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BankMiddlewareServiceImpl bankMiddlewareService;

    private BankMiddlewareRequest request;
    private BankMiddlewareResponse expectedResponse;
    private String unit = "unit1";
    private String channel = "web";
    private String acceptLanguage = "en-US";
    private String serviceId = "service123";
    private String screenId = "screen456";
    private String moduleId = "module789";
    private String subModuleId = "subModule101";

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        // Initialize test data
        request = BankMiddlewareRequest.builder()
                .serviceName("testService")
                .parameters(Arrays.asList(
                        BankMiddlewareRequest.Parameter.builder().fieldName("key1").fieldValue("value1").build(),
                        BankMiddlewareRequest.Parameter.builder().fieldName("key2").fieldValue("value2").build()
                ))
                .build();

        expectedResponse = BankMiddlewareResponse.builder()
                .status("SUCCESS")
                .message("Request processed successfully")
                .timestamp(LocalDateTime.now())
                .bankResponse(BankResponse.builder()
                        .referenceNum("REF123")
                        .customerNumber("CUST456")
                        .correlationId("CORR789")
                        .returnStatus(ReturnStatus.builder()
                                .returnCode("200")
                                .returnCodeDesc("OK")
                                .build())
                        .returnStatusProvider(ReturnStatusProvider.builder()
                                .returnCodeProvider("PROV200")
                                .returnCodeDescProvider("Provider OK")
                                .build())
                        .build())
                .errors(Arrays.asList())
                .build();

        // Explicitly set bankMiddlewareUrl to avoid null
        Field urlField = BankMiddlewareServiceImpl.class.getDeclaredField("bankMiddlewareUrl");
        urlField.setAccessible(true);
        urlField.set(bankMiddlewareService, "http://localhost:8080");
    }

    @Test
    void testCallBankMiddleware_SuccessfulResponse() {
        // Arrange
        ResponseEntity<BankMiddlewareResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(
                eq("http://localhost:8080/api/v1/bank-middleware"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(BankMiddlewareResponse.class)
        )).thenReturn(responseEntity);

        // Act
        BankMiddlewareResponse response = bankMiddlewareService.callBankMiddleware(
                unit, channel, acceptLanguage, serviceId, screenId, moduleId, subModuleId, request);

        // Assert
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("Request processed successfully", response.getMessage());
        assertEquals("REF123", response.getBankResponse().getReferenceNum());
        assertEquals("CUST456", response.getBankResponse().getCustomerNumber());
        assertEquals("CORR789", response.getBankResponse().getCorrelationId());
        assertEquals("200", response.getBankResponse().getReturnStatus().getReturnCode());
        assertEquals("PROV200", response.getBankResponse().getReturnStatusProvider().getReturnCodeProvider());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(BankMiddlewareResponse.class));

    }

    @Test
    void testCallBankMiddleware_HttpClientError() {
        // Arrange
        when(restTemplate.exchange(
                eq("http://localhost:8080/api/v1/bank-middleware"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(BankMiddlewareResponse.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bankMiddlewareService.callBankMiddleware(
                    unit, channel, acceptLanguage, serviceId, screenId, moduleId, subModuleId, request);
        });
        assertEquals("Failed to call bank middleware API", exception.getMessage());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(BankMiddlewareResponse.class));
    }

    @Test
    void testCallBankMiddleware_GenericException() {
        // Arrange
        when(restTemplate.exchange(
                eq("http://localhost:8080/api/v1/bank-middleware"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(BankMiddlewareResponse.class)
        )).thenThrow(new RuntimeException("Network error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bankMiddlewareService.callBankMiddleware(
                    unit, channel, acceptLanguage, serviceId, screenId, moduleId, subModuleId, request);
        });
        assertEquals("Failed to call bank middleware API", exception.getMessage());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(BankMiddlewareResponse.class));
    }

    @Test
    void testCallBankMiddleware_NullResponseBody() {
        // Arrange
        ResponseEntity<BankMiddlewareResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(
                eq("http://localhost:8080/api/v1/bank-middleware"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(BankMiddlewareResponse.class)
        )).thenReturn(responseEntity);

        // Act
        BankMiddlewareResponse response = bankMiddlewareService.callBankMiddleware(
                unit, channel, acceptLanguage, serviceId, screenId, moduleId, subModuleId, request);

        // Assert
        assertNull(response);
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(BankMiddlewareResponse.class));
    }

    @Test
    void testCallBankMiddleware_VerifyHeaders() {
        // Arrange
        ResponseEntity<BankMiddlewareResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(BankMiddlewareResponse.class)
        )).thenAnswer(invocation -> {
            HttpEntity<?> entity = invocation.getArgument(2);
            HttpHeaders headers = entity.getHeaders();
            assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
            assertEquals(unit, headers.getFirst("unit"));
            assertEquals(channel, headers.getFirst("channel"));
            assertEquals(acceptLanguage, headers.getFirst("accept-language"));
            assertEquals(serviceId, headers.getFirst("serviceId"));
            assertEquals(screenId, headers.getFirst("screenId"));
            assertEquals(moduleId, headers.getFirst("moduleId"));
            assertEquals(subModuleId, headers.getFirst("subModuleId"));
            return responseEntity;
        });

        // Act
        BankMiddlewareResponse response = bankMiddlewareService.callBankMiddleware(
                unit, channel, acceptLanguage, serviceId, screenId, moduleId, subModuleId, request);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(BankMiddlewareResponse.class));
    }
}