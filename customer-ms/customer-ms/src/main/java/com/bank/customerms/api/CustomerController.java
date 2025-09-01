package com.bank.customerms.api;

import com.bank.customerms.contract.api.ClientesApi;
import com.bank.customerms.contract.model.CustomerCreateDto;
import com.bank.customerms.contract.model.CustomerDto;
import com.bank.customerms.contract.model.CustomerPage;
import com.bank.customerms.contract.model.CustomerUpdateDto;

import com.bank.customerms.domain.Customer;
import com.bank.customerms.service.CustomerService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerController implements ClientesApi {

    private final CustomerService service;

    // GET /api/v1/clientes?q=&page=&size=&sort=
    @Override
    public ResponseEntity<CustomerPage> listCustomers(String q, Integer page, Integer size, String sort) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size < 1) ? 10 : Math.min(size, 50);
        Sort sortSpec = parseSort(sort);
        Pageable pageable = PageRequest.of(p, s, sortSpec);

        Page<Customer> pageDomain = service.list(q, pageable);

        CustomerPage resp = new CustomerPage();
        resp.setNumber(pageDomain.getNumber());
        resp.setSize(pageDomain.getSize());
        resp.setTotalElements(pageDomain.getTotalElements());
        resp.setTotalPages(pageDomain.getTotalPages());
        resp.setFirst(pageDomain.isFirst());
        resp.setLast(pageDomain.isLast());
        resp.setSort(sortSpec.isSorted() ? sortSpec.toString() : "");
        resp.setContent(pageDomain.getContent().stream().map(this::toDto).toList());

        return ResponseEntity.ok(resp);
    }

    // POST /api/v1/clientes
    @Override
    public ResponseEntity<CustomerDto> createCustomer(CustomerCreateDto body) {
        var created = service.create(
                new com.bank.customerms.api.dto.CustomerDtos.CustomerCreateDto(
                        body.getFirstName(),
                        body.getLastName(),
                        body.getDni(),
                        body.getEmail()
                )
        );
        return ResponseEntity.status(201).body(toDto(created));
    }

    // GET /api/v1/clientes/{id}
    @Override
    public ResponseEntity<CustomerDto> getCustomer(Long id) {
        return ResponseEntity.ok(toDto(service.get(id)));
    }

    // PUT /api/v1/clientes/{id}
    @Override
    public ResponseEntity<CustomerDto> updateCustomer(Long id, CustomerUpdateDto body) {
        var updated = service.update(
                id,
                new com.bank.customerms.api.dto.CustomerDtos.CustomerUpdateDto(
                        body.getFirstName(),
                        body.getLastName(),
                        body.getEmail()
                )
        );
        return ResponseEntity.ok(toDto(updated));
    }

    // DELETE /api/v1/clientes/{id}
    @Override
    public ResponseEntity<Void> deleteCustomer(Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- helpers ----------
    private CustomerDto toDto(Customer c) {
        var dto = new CustomerDto();
        dto.setId(c.getId());
        dto.setFirstName(c.getFirstName());
        dto.setLastName(c.getLastName());
        dto.setDni(c.getDni());
        dto.setEmail(c.getEmail());
        return dto;
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) return Sort.by(Sort.Direction.DESC, "id");
        try {
            var parts = sort.split(",", 2);
            var field = parts[0].trim();
            var dir = (parts.length > 1 ? parts[1].trim() : "asc");
            return Sort.by("desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC, field);
        } catch (Exception e) {
            return Sort.by(Sort.Direction.DESC, "id");
        }
    }
}
