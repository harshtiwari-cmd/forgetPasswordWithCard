package com.dukhan.forgot.domain.repository;

import com.dukhan.forgot.domain.model.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testFindByCustomerId_Success() {
        // Given
        Customer customer = Customer.builder()
                .userNo(1L)
                .customerId(123456L)
                .userId("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNo("+97433333335")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        entityManager.persistAndFlush(customer);

        // When
        Optional<Customer> result = customerRepository.findByCustomerId(123456L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(123456L, result.get().getCustomerId());
        assertEquals("testuser", result.get().getUserId());
        assertEquals("test@example.com", result.get().getEmail());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
    }

    @Test
    void testFindByCustomerId_NotFound() {
        // When
        Optional<Customer> result = customerRepository.findByCustomerId(999999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindUsernameByCustomerId_Success() {
        // Given
        Customer customer = Customer.builder()
                .userNo(1L)
                .customerId(123456L)
                .userId("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNo("+97433333335")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        entityManager.persistAndFlush(customer);

        // When
        Optional<String> result = customerRepository.findUsernameByCustomerId(123456L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get());
    }

    @Test
    void testFindUsernameByCustomerId_NotFound() {
        // When
        Optional<String> result = customerRepository.findUsernameByCustomerId(999999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByCustomerId_MultipleCustomers() {
        // Given
        Customer customer1 = Customer.builder()
                .userNo(1L)
                .customerId(123456L)
                .userId("user1")
                .email("user1@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNo("+97433333335")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Customer customer2 = Customer.builder()
                .userNo(2L)
                .customerId(789012L)
                .userId("user2")
                .email("user2@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .phoneNo("+97433333336")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        entityManager.persistAndFlush(customer1);
        entityManager.persistAndFlush(customer2);

        // When
        Optional<Customer> result1 = customerRepository.findByCustomerId(123456L);
        Optional<Customer> result2 = customerRepository.findByCustomerId(789012L);

        // Then
        assertTrue(result1.isPresent());
        assertEquals(123456L, result1.get().getCustomerId());
        assertEquals("user1", result1.get().getUserId());

        assertTrue(result2.isPresent());
        assertEquals(789012L, result2.get().getCustomerId());
        assertEquals("user2", result2.get().getUserId());
    }

    @Test
    void testFindUsernameByCustomerId_MultipleCustomers() {
        // Given
        Customer customer1 = Customer.builder()
                .userNo(1L)
                .customerId(123456L)
                .userId("user1")
                .email("user1@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNo("+97433333335")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Customer customer2 = Customer.builder()
                .userNo(2L)
                .customerId(789012L)
                .userId("user2")
                .email("user2@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .phoneNo("+97433333336")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        entityManager.persistAndFlush(customer1);
        entityManager.persistAndFlush(customer2);

        // When
        Optional<String> result1 = customerRepository.findUsernameByCustomerId(123456L);
        Optional<String> result2 = customerRepository.findUsernameByCustomerId(789012L);

        // Then
        assertTrue(result1.isPresent());
        assertEquals("user1", result1.get());

        assertTrue(result2.isPresent());
        assertEquals("user2", result2.get());
    }

    @Test
    void testFindByCustomerId_NullCustomerId() {
        // When
        Optional<Customer> result = customerRepository.findByCustomerId(null);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindUsernameByCustomerId_NullCustomerId() {
        // When
        Optional<String> result = customerRepository.findUsernameByCustomerId(null);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByCustomerId_ZeroCustomerId() {
        // When
        Optional<Customer> result = customerRepository.findByCustomerId(0L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindUsernameByCustomerId_ZeroCustomerId() {
        // When
        Optional<String> result = customerRepository.findUsernameByCustomerId(0L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByCustomerId_NegativeCustomerId() {
        // When
        Optional<Customer> result = customerRepository.findByCustomerId(-1L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindUsernameByCustomerId_NegativeCustomerId() {
        // When
        Optional<String> result = customerRepository.findUsernameByCustomerId(-1L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testCustomerEntity_AllFields() {
        // Given
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
                .dateOfBirth(java.time.LocalDate.of(1990, 1, 1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        entityManager.persistAndFlush(customer);

        // When
        Optional<Customer> result = customerRepository.findByCustomerId(123456L);

        // Then
        assertTrue(result.isPresent());
        Customer foundCustomer = result.get();
        assertEquals(1L, foundCustomer.getUserNo());
        assertEquals(123456L, foundCustomer.getCustomerId());
        assertEquals("testuser", foundCustomer.getUserId());
        assertEquals("test@example.com", foundCustomer.getEmail());
        assertEquals("John", foundCustomer.getFirstName());
        assertEquals("Doe", foundCustomer.getLastName());
        assertEquals("Michael", foundCustomer.getMiddleName());
        assertEquals("+97433333335", foundCustomer.getPhoneNo());
        assertEquals("123 Main St", foundCustomer.getAddress());
        assertEquals("Doha", foundCustomer.getCity());
        assertEquals("Doha", foundCustomer.getState());
        assertEquals("Qatar", foundCustomer.getCountry());
        assertEquals("12345", foundCustomer.getPostalCode());
        assertEquals("http://example.com/pic.jpg", foundCustomer.getProfilePictureUrl());
        assertEquals("DOMAIN123", foundCustomer.getDomainId());
        assertEquals(java.time.LocalDate.of(1990, 1, 1), foundCustomer.getDateOfBirth());
        assertNotNull(foundCustomer.getCreatedAt());
        assertNotNull(foundCustomer.getUpdatedAt());
    }
}
