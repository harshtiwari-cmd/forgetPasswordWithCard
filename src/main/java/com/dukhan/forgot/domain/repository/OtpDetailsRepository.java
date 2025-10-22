package com.dukhan.forgot.domain.repository;

import com.dukhan.forgot.domain.model.entity.OtpDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OtpDetailsRepository extends JpaRepository<OtpDetails, String> {
    
    @Query("SELECT o FROM OtpDetails o WHERE o.userId = :userId AND o.otpStatus = 'ACTIVE' ORDER BY o.createdTime DESC")
    List<OtpDetails> findActiveOtpByUserId(@Param("userId") String userId);
    
    @Query("SELECT o FROM OtpDetails o WHERE o.userId = :userId AND o.otpStatus = 'ACTIVE' AND o.noOfAttempts >= :maxAttempts")
    List<OtpDetails> findBlockedOtpByUserId(@Param("userId") String userId, @Param("maxAttempts") Integer maxAttempts);
}
