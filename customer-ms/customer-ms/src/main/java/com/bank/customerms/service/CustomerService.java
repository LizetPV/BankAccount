package com.bank.customerms.service;

import com.bank.customerms.api.dto.CustomerDtos.CustomerCreateDto;
import com.bank.customerms.api.dto.CustomerDtos.CustomerUpdateDto;
import com.bank.customerms.domain.Customer;
import com.bank.customerms.repository.CustomerRepository;
import com.bank.customerms.client.AccountClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repo;
    private final AccountClient accountClient;

    public Customer create(CustomerCreateDto dto) {
        if (repo.existsByDni(dto.dni())) {
            throw new IllegalArgumentException("DNI already exists");
        }
        var c = Customer.builder()
                .firstName(dto.firstName().strip())
                .lastName(dto.lastName().strip())
                .dni(dto.dni().strip())
                .email(dto.email().strip())
                .build();
        return repo.save(c);
    }

    public List<Customer> list() {
        return repo.findAll();
    }

    public Customer get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Customer not found"));
    }

    public Customer update(Long id, CustomerUpdateDto dto) {
        var c = get(id);
        c.setFirstName(dto.firstName().strip());
        c.setLastName(dto.lastName().strip());
        c.setEmail(dto.email().strip());
        return repo.save(c);
    }

    public void delete(Long id) {
        var c = get(id);
        if (accountClient.hasAccounts(c.getId())) {
            throw new IllegalStateException("Customer has active accounts");
        }
        repo.deleteById(id);
    }

    public Page<Customer> list(String q, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return repo.findAll(pageable);
        }
        var k = q.strip();
        return repo.findByDniContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                k, k, k, pageable);
    }
}
