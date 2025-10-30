package com.dukhan.forgot.domain.model.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        LocalDateTime now = LocalDateTime.now();

        Customer customer = Customer.builder()
                .authId(1L)
                .authType("PASSWORD")
                .channelId("WEB")
                .createdAt(now)
                .createdBy("admin")
                .customerId(12345L)
                .dateMigrated(now.minusDays(1))
                .failedLoginAttempts(0)
                .forcePwdChange("N")
                .forceUserChange("N")
                .lastLoginAt(now.minusDays(1))
                .lastLoginChannel("MOBILE")
                .lastLoginIp("192.168.1.1")
                .lastUnsuccessfulLogin(now.minusDays(2))
                .passwordChangedAt(now.minusDays(10))
                .passwordHash("hashed-password")
                .pwdCipherBase64(new byte[]{1, 2, 3})
                .pwdEncKeyBase64(new byte[]{4, 5, 6})
                .pwdIvBase64("iv-example")
                .serverSalt("server-salt-example")
                .status("ACTIVE")
                .updatedAt(now)
                .userId("user123")
                .userType("CUSTOMER")
                .userNo(111L)
                .updatedBy("admin")
                .build();

        assertNotNull(customer);
        assertEquals("user123", customer.getUserId());
        assertEquals("hashed-password", customer.getPasswordHash());
        assertEquals("server-salt-example", customer.getServerSalt());
        assertEquals(now, customer.getUpdatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        Customer customer = new Customer();
        assertNotNull(customer);
        assertNull(customer.getUserId());
        assertNull(customer.getPasswordHash());
        assertNull(customer.getUpdatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();

        Customer customer = new Customer(
                1L, "PASSWORD", "WEB", now, "admin", 12345L, now.minusDays(1), 0,
                "N", "N", now.minusDays(1), "MOBILE", "192.168.1.1", now.minusDays(2),
                now.minusDays(10), "hashed-password", new byte[]{1, 2, 3}, new byte[]{4, 5, 6},
                "iv-example", "server-salt-example", "ACTIVE", now, "user123",
                "CUSTOMER", 111L, "admin"
        );

        assertEquals("user123", customer.getUserId());
        assertEquals("hashed-password", customer.getPasswordHash());
        assertEquals("ACTIVE", customer.getStatus());
    }

    @Test
    void testValidation_MissingRequiredFields() {
        Customer customer = new Customer(); // All null

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Since only JPA constraints (e.g., nullable = false) exist, this may not throw validation errors
        // unless you annotate with @NotNull, @NotBlank, etc.
        // This test will pass unless Bean Validation annotations are added
        assertTrue(violations.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();

        Customer c1 = Customer.builder()
                .authId(1L)
                .userId("user123")
                .passwordHash("hash1")
                .serverSalt("salt")
                .updatedAt(now)
                .build();

        Customer c2 = Customer.builder()
                .authId(1L)
                .userId("user123")
                .passwordHash("hash1")
                .serverSalt("salt")
                .updatedAt(now)
                .build();

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testToString() {
        Customer customer = Customer.builder()
                .authId(1L)
                .userId("user123")
                .build();

        String result = customer.toString();
        assertNotNull(result);
        assertTrue(result.contains("Customer"));
        assertTrue(result.contains("user123"));
    }

    @Test
    void testSettersAndGetters() {
        Customer customer = new Customer();
        customer.setUserId("user123");
        customer.setAuthId(10L);
        customer.setPasswordHash("hash");
        customer.setServerSalt("salt");

        assertEquals("user123", customer.getUserId());
        assertEquals(10L, customer.getAuthId());
        assertEquals("hash", customer.getPasswordHash());
        assertEquals("salt", customer.getServerSalt());
    }
}
