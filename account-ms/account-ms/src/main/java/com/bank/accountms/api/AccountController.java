package com.bank.accountms.api;

import com.bank.accountms.api.dto.AccountDtos.*;
import com.bank.accountms.api.mapper.AccountMapper;
import com.bank.accountms.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Page;


import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
//@RequestMapping("/cuentas")
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;


    @GetMapping
    public Page<AccountDto> list(
            @RequestParam(required = false) Long customerId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {

        var safePageable = enforceMax(pageable, 50);
        return service.list(customerId, safePageable).map(AccountMapper::toDto);
    }

    private Pageable enforceMax(Pageable pageable, int maxSize) {
        int size = Math.min(pageable.getPageSize(), maxSize);
        Sort sort = pageable.getSort();
        return PageRequest.of(pageable.getPageNumber(), size, sort);
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

    @GetMapping("/total-balance")
    public CompletableFuture<Double> total(@RequestParam Long customerId) {
        return service.totalBalanceAsync(customerId);
    }


}
