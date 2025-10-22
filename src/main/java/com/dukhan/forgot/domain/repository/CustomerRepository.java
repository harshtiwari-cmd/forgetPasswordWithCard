package com.dukhan.forgot.domain.repository;

import com.dukhan.forgot.domain.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    @Query("SELECT c FROM Customer c WHERE c.customerId = :customerId")
    Optional<Customer> findByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT c.userId FROM Customer c WHERE c.customerId = :customerId")
    Optional<String> findUsernameByCustomerId(@Param("customerId") Long customerId);
    
    // Method to save customer (inherited from JpaRepository, but adding for clarity)
    // Customer save(Customer customer);
}
