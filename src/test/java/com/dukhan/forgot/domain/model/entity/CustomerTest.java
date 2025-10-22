package com.dukhan.forgot.domain.model.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testBuilder() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        // When
        Customer customer = Customer.builder()
                .userNo(1L)
                .customerId(123456L)
                .userId("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .middleName("Michael")
                .phoneNo("+97433333335")
                .address("123 Main St")
                .city("Doha")
                .state("Doha")
                .country("Qatar")
                .postalCode("12345")
                .profilePictureUrl("http://example.com/pic.jpg")
                .domainId("DOMAIN123")
                .dateOfBirth(birthDate)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Then
        assertNotNull(customer);
        assertEquals(1L, customer.getUserNo());
        assertEquals(123456L, customer.getCustomerId());
        assertEquals("testuser", customer.getUserId());
        assertEquals("test@example.com", customer.getEmail());
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("Michael", customer.getMiddleName());
        assertEquals("+97433333335", customer.getPhoneNo());
        assertEquals("123 Main St", customer.getAddress());
        assertEquals("Doha", customer.getCity());
        assertEquals("Doha", customer.getState());
        assertEquals("Qatar", customer.getCountry());
        assertEquals("12345", customer.getPostalCode());
        assertEquals("http://example.com/pic.jpg", customer.getProfilePictureUrl());
        assertEquals("DOMAIN123", customer.getDomainId());
        assertEquals(birthDate, customer.getDateOfBirth());
        assertEquals(now, customer.getCreatedAt());
        assertEquals(now, customer.getUpdatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        // When
        Customer customer = new Customer();

        // Then
        assertNotNull(customer);
        assertNull(customer.getUserNo());
        assertNull(customer.getCustomerId());
        assertNull(customer.getUserId());
        assertNull(customer.getEmail());
        assertNull(customer.getFirstName());
        assertNull(customer.getLastName());
        assertNull(customer.getMiddleName());
        assertNull(customer.getPhoneNo());
        assertNull(customer.getAddress());
        assertNull(customer.getCity());
        assertNull(customer.getState());
        assertNull(customer.getCountry());
        assertNull(customer.getPostalCode());
        assertNull(customer.getProfilePictureUrl());
        assertNull(customer.getDomainId());
        assertNull(customer.getDateOfBirth());
        assertNull(customer.getCreatedAt());
        assertNull(customer.getUpdatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        // When
        Customer customer = new Customer(
                1L, "123 Main St", "Doha", "Qatar", now, 123456L, birthDate, "DOMAIN123",
                "test@example.com", "John", "Doe", "Michael", "+97433333335", "12345",
                "http://example.com/pic.jpg", "Doha", now, "testuser"
        );

        // Then
        assertNotNull(customer);
        assertEquals(1L, customer.getUserNo());
        assertEquals(123456L, customer.getCustomerId());
        assertEquals("testuser", customer.getUserId());
        assertEquals("test@example.com", customer.getEmail());
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("Michael", customer.getMiddleName());
        assertEquals("+97433333335", customer.getPhoneNo());
        assertEquals("123 Main St", customer.getAddress());
        assertEquals("Doha", customer.getCity());
        assertEquals("Doha", customer.getState());
        assertEquals("Qatar", customer.getCountry());
        assertEquals("12345", customer.getPostalCode());
        assertEquals("http://example.com/pic.jpg", customer.getProfilePictureUrl());
        assertEquals("DOMAIN123", customer.getDomainId());
        assertEquals(birthDate, customer.getDateOfBirth());
        assertEquals(now, customer.getCreatedAt());
        assertEquals(now, customer.getUpdatedAt());
    }

    @Test
    void testValidation_ValidCustomer() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Customer customer = Customer.builder()
                .userNo(1L)
                .customerId(123456L)
                .userId("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNo("+97433333335")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_BlankEmail() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Customer customer = Customer.builder()
                .userNo(1L)
                .customerId(123456L)
                .userId("testuser")
                .email("")
                .firstName("John")
                .lastName("Doe")
                .phoneNo("+97433333335")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
    }

    @Test
    void testValidation_InvalidEmail() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Customer customer = Customer.builder()
                .userNo(1L)
                .customerId(123456L)
                .userId("testuser")
                .email("invalid-email")
                .firstName("John")
                .lastName("Doe")
                .phoneNo("+97433333335")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email should be valid")));
    }

    @Test
    void testValidation_BlankUserId() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Customer customer = Customer.builder()
                .userNo(1L)
                .customerId(123456L)
                .userId("")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNo("+97433333335")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("User ID is required")));
    }

    @Test
    void testValidation_UserIdTooLong() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        String longUserId = "a".repeat(51); // 51 characters
        Customer customer = Customer.builder()
                .userNo(1L)
                .customerId(123456L)
                .userId(longUserId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNo("+97433333335")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("size must be between 0 and 50")));
    }

    @Test
    void testValidation_EmailTooLong() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        String longEmail = "a".repeat(151) + "@example.com"; // 151 characters
        Customer customer = Customer.builder()
                .userNo(1L)
                .customerId(123456L)
                .userId("testuser")
                .email(longEmail)
                .firstName("John")
                .lastName("Doe")
                .phoneNo("+97433333335")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("size must be between 0 and 150")));
    }

    @Test
    void testSettersAndGetters() {
        // Given
        Customer customer = new Customer();
        LocalDateTime now = LocalDateTime.now();
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        // When
        customer.setUserNo(1L);
        customer.setCustomerId(123456L);
        customer.setUserId("testuser");
        customer.setEmail("test@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setMiddleName("Michael");
        customer.setPhoneNo("+97433333335");
        customer.setAddress("123 Main St");
        customer.setCity("Doha");
        customer.setState("Doha");
        customer.setCountry("Qatar");
        customer.setPostalCode("12345");
        customer.setProfilePictureUrl("http://example.com/pic.jpg");
        customer.setDomainId("DOMAIN123");
        customer.setDateOfBirth(birthDate);
        customer.setCreatedAt(now);
        customer.setUpdatedAt(now);

        // Then
        assertEquals(1L, customer.getUserNo());
        assertEquals(123456L, customer.getCustomerId());
        assertEquals("testuser", customer.getUserId());
        assertEquals("test@example.com", customer.getEmail());
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("Michael", customer.getMiddleName());
        assertEquals("+97433333335", customer.getPhoneNo());
        assertEquals("123 Main St", customer.getAddress());
        assertEquals("Doha", customer.getCity());
        assertEquals("Doha", customer.getState());
        assertEquals("Qatar", customer.getCountry());
        assertEquals("12345", customer.getPostalCode());
        assertEquals("http://example.com/pic.jpg", customer.getProfilePictureUrl());
        assertEquals("DOMAIN123", customer.getDomainId());
        assertEquals(birthDate, customer.getDateOfBirth());
        assertEquals(now, customer.getCreatedAt());
        assertEquals(now, customer.getUpdatedAt());
    }

    @Test
    void testToString() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Customer customer = Customer.builder()
                .userNo(1L)
                .customerId(123456L)
                .userId("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNo("+97433333335")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        String toString = customer.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Customer"));
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Customer customer1 = Customer.builder()
                .userNo(1L)
                .customerId(123456L)
                .userId("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNo("+97433333335")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Customer customer2 = Customer.builder()
                .userNo(1L)
                .customerId(123456L)
                .userId("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNo("+97433333335")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When & Then
        assertEquals(customer1, customer2);
        assertEquals(customer1.hashCode(), customer2.hashCode());
    }

    @Test
    void testMinimalValidCustomer() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Customer customer = Customer.builder()
                .userNo(1L)
                .customerId(123456L)
                .userId("testuser")
                .email("test@example.com")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testCustomerWithAllOptionalFields() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        Customer customer = Customer.builder()
                .userNo(1L)
                .customerId(123456L)
                .userId("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .middleName("Michael")
                .phoneNo("+97433333335")
                .address("123 Main St, Building 1, Apartment 2A")
                .city("Doha")
                .state("Doha")
                .country("Qatar")
                .postalCode("12345")
                .profilePictureUrl("https://example.com/profile/pic.jpg")
                .domainId("DOMAIN123")
                .dateOfBirth(birthDate)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertTrue(violations.isEmpty());
        assertNotNull(customer.getFirstName());
        assertNotNull(customer.getLastName());
        assertNotNull(customer.getMiddleName());
        assertNotNull(customer.getPhoneNo());
        assertNotNull(customer.getAddress());
        assertNotNull(customer.getCity());
        assertNotNull(customer.getState());
        assertNotNull(customer.getCountry());
        assertNotNull(customer.getPostalCode());
        assertNotNull(customer.getProfilePictureUrl());
        assertNotNull(customer.getDomainId());
        assertNotNull(customer.getDateOfBirth());
    }
}
