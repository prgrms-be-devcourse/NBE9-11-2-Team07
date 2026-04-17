package com.back.mozu.domain.customer.repository;

import com.back.mozu.domain.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByProviderId(String providerId);
    boolean existsByEmail(String email);
}
