package com.dukhan.forgot.domain.repository;

import com.dukhan.forgot.domain.model.entity.CardValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardValidationRepository extends JpaRepository<CardValidation, Long> {
    
    @Query("SELECT cv FROM CardValidation cv WHERE cv.cardNumber = :cardNumber")
    Optional<CardValidation> findByCardNumber(@Param("cardNumber") String cardNumber);
}
