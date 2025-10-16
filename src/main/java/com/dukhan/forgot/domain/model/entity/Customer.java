package com.dukhan.forgot.domain.model.entity;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rbx_t_user_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    private Long userNo;

    @Column(name = "address", columnDefinition = "text")
    private String address;

    @Size(max = 100)
    @Column(name = "city", length = 100)
    private String city;

    @Size(max = 100)
    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Size(max = 64)
    @Column(name = "domain_id", length = 64)
    private String domainId;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 150)
    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @Size(max = 60)
    @Column(name = "first_name", length = 60)
    private String firstName;

    @Size(max = 60)
    @Column(name = "last_name", length = 60)
    private String lastName;

    @Size(max = 60)
    @Column(name = "middle_name", length = 60)
    private String middleName;

    @Size(max = 20)
    @Column(name = "phone_no", length = 20)
    private String phoneNo;

    @Size(max = 20)
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Size(max = 500)
    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    @Size(max = 100)
    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @NotBlank(message = "User ID is required")
    @Size(max = 50)
    @Column(name = "user_id", length = 50, nullable = false, unique = true)
    private String userId;
}