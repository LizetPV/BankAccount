package com.bank.accountms.api;

import com.bank.accountms.api.dto.AccountDtos.*;
import com.bank.accountms.api.mapper.AccountMapper;
import com.bank.accountms.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @GetMapping
    public List<AccountDto> list(@RequestParam(required = false) Long customerId) {
        return service.list(customerId).stream().map(AccountMapper::toDto).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto create(@Valid @RequestBody AccountCreateDto dto) {
        return AccountMapper.toDto(service.create(dto));
    }

    @GetMapping("/{id}")
    public AccountDto get(@PathVariable Long id) {
        return AccountMapper.toDto(service.get(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("/{id}/depositar")
    public AccountDto deposit(@PathVariable Long id, @Valid @RequestBody AmountDto dto) {
        return AccountMapper.toDto(service.deposit(id, dto));
    }

    @PutMapping("/{id}/retirar")
    public AccountDto withdraw(@PathVariable Long id, @Valid @RequestBody AmountDto dto) {
        return AccountMapper.toDto(service.withdraw(id, dto));
    }

    // opcional: suma asíncrona de saldos
    @GetMapping("/total-balance")
    public CompletableFuture<Double> total(@RequestParam Long customerId) {
        return service.totalBalanceAsync(customerId);
    }
}
