package com.dukhan.forgot.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "card_bin_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardBinMaster {
    
    @Id
    @Column(name = "code", length = 10)
    @NotBlank(message = "Code is required")
    @Size(max = 10, message = "Code cannot exceed 10 characters")
    private String code;
    
    @Column(name = "bin", length = 20, nullable = false)
    @NotBlank(message = "BIN is required")
    @Size(max = 20, message = "BIN cannot exceed 20 characters")
    @Pattern(regexp = "^\\d+$", message = "BIN must contain only digits")
    private String bin;
    
    @Column(name = "product_type", length = 100)
    @Size(max = 100, message = "Product type cannot exceed 100 characters")
    private String productType;
    
    @Column(name = "card_type", length = 20)
    @Size(max = 20, message = "Card type cannot exceed 20 characters")
    private String cardType;

    @Column(name = "status", length = 20, nullable = false)
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be ACTIVE or INACTIVE")
    private String status;
}
