package com.dukhan.forgot.domain.repository;

import com.dukhan.forgot.domain.model.entity.CardBinMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardBinMasterRepository extends JpaRepository<CardBinMaster, String> {
    
    @Query("SELECT c FROM CardBinMaster c WHERE c.bin = :bin")
    List<CardBinMaster> findByBin(@Param("bin") String bin);
    
    @Query("SELECT c FROM CardBinMaster c WHERE c.bin LIKE :binPrefix%")
    Optional<CardBinMaster> findByBinStartingWith(@Param("binPrefix") String binPrefix);
}
