package com.bank.repository;

import com.bank.domain.Customer;
import java.util.*;


public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findByDni(String dni);
}