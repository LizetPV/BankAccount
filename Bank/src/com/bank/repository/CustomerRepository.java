package com.bank.repository;

import com.bank.domain.Customer;
import java.util.*;


public interface CustomerRepository {
    Customer save(Customer customer);           // crea/actualiza
    Optional<Customer> findByDni(String dni);           // puede no existir -> Optional
}