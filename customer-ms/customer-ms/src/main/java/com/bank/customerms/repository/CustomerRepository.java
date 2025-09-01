package com.bank.customerms.repository;

import com.bank.customerms.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByDni(String dni);
    Optional<Customer> findByDni(String dni);

    Page<Customer> findByDniContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String dni, String first, String last, Pageable pageable);
}


