package com.dukhan.forgot.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rbx_t_card_validation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardValidation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "card_number", length = 16, nullable = false)
    private String cardNumber;
    
    @Column(name = "attempts", nullable = false)
    private Integer attempts;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public static final int MAX_FAILED_ATTEMPTS = 3;
    

    public boolean incrementAttempts() {
        if (this.attempts == null) {
            this.attempts = 0;
        }
        
        this.attempts++;
        this.updatedAt = LocalDateTime.now();
        
        return this.attempts >= MAX_FAILED_ATTEMPTS;
    }
    

    public void resetAttempts() {
        this.attempts = 0;
        this.updatedAt = LocalDateTime.now();
    }
    

    public boolean isBlocked() {
        return this.attempts != null && this.attempts >= MAX_FAILED_ATTEMPTS;
    }
}
