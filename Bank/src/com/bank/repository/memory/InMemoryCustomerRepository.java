package com.bank.repository.memory;

import com.bank.domain.Customer;
import com.bank.repository.CustomerRepository;


import java.util.*;


public class InMemoryCustomerRepository implements CustomerRepository {
    private final Map<String, Customer> byDni = new HashMap<>();


    @Override
    public Customer save(Customer customer) {
        byDni.put(customer.getDni(), customer); // DNI unique
        return customer;
    }


    @Override
    public Optional<Customer> findByDni(String dni) {
        return Optional.ofNullable(byDni.get(dni));
    }
}