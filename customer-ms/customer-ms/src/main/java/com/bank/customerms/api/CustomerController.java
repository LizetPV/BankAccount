package com.bank.customerms.api;

import com.bank.customerms.api.dto.CustomerDtos.*;
import com.bank.customerms.api.mapper.CustomerMapper;
import com.bank.customerms.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @GetMapping
    public List<CustomerDto> list() {
        return service.list().stream().map(CustomerMapper::toDto).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto create(@Valid @RequestBody CustomerCreateDto dto) {
        return CustomerMapper.toDto(service.create(dto));
    }

    @GetMapping("/{id}")
    public CustomerDto get(@PathVariable Long id) {
        return CustomerMapper.toDto(service.get(id));
    }

    @PutMapping("/{id}")
    public CustomerDto update(@PathVariable Long id, @Valid @RequestBody CustomerUpdateDto dto) {
        return CustomerMapper.toDto(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
