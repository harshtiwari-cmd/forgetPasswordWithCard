package com.dukhan.forgot.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "card_bin_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardBinMaster {
    
    @Id
    @Column(name = "code", length = 10)
    private String code;
    
    @Column(name = "bin", length = 20)
    private String bin;
    
    @Column(name = "product_type", length = 100)
    private String productType;
    
    @Column(name = "card_type", length = 20)
    private String cardType;

    @Column(name = "status", length = 20)
    private String status;
}
