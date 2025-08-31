package com.bank.customerms.repository;

import com.bank.customerms.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByDni(String dni);
    Optional<Customer> findByDni(String dni);
}
