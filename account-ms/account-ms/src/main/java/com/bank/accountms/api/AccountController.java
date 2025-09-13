package com.bank.accountms.api;

import com.bank.accountms.contract.api.CuentasApi;
import com.bank.accountms.contract.model.AccountCreateDto;
import com.bank.accountms.contract.model.AccountDto;
import com.bank.accountms.contract.model.AccountPage;
import com.bank.accountms.contract.model.AmountDto;
import com.bank.accountms.domain.Account;
import com.bank.accountms.service.AccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController implements CuentasApi {

    private final AccountService service;

    @Override
    public ResponseEntity<AccountPage> listAccounts(Long customerId, Integer page, Integer size, String sort) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size < 1) ? 10 : Math.min(size, 50);
        Sort sortSpec = parseSort(sort);
        Pageable pageable = PageRequest.of(p, s, sortSpec);

        Page<Account> pageDomain = service.list(customerId, pageable);

        AccountPage resp = new AccountPage();
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

    @Override
    public ResponseEntity<AccountDto> createAccount(AccountCreateDto body) {
        var created = service.create(
                new com.bank.accountms.api.dto.AccountDtos.AccountCreateDto(
                        body.getCustomerId(),
                        body.getAccountType().getValue(),   // "SAVINGS" | "CHECKING"
                        body.getInitialDeposit()
                )
        );
        return ResponseEntity.status(201).body(toDto(created));
    }

    @Override
    public ResponseEntity<AccountDto> getAccount(Long id) {
        return ResponseEntity.ok(toDto(service.get(id)));
    }

    @Override
    public ResponseEntity<Void> deleteAccount(Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AccountDto> deposit(Long id, AmountDto body) {
        var updated = service.deposit(id,
                new com.bank.accountms.api.dto.AccountDtos.AmountDto(body.getAmount())
        );
        return ResponseEntity.ok(toDto(updated));
    }

    @Override
    public ResponseEntity<AccountDto> withdraw(Long id, AmountDto body) {
        var updated = service.withdraw(id,
                new com.bank.accountms.api.dto.AccountDtos.AmountDto(body.getAmount())
        );
        return ResponseEntity.ok(toDto(updated));
    }

    @Override
    public ResponseEntity<Double> totalBalance(Long customerId) {
        return ResponseEntity.ok(service.totalBalanceAsync(customerId).join());
    }

    @Override
    public ResponseEntity<AccountDto> getAccountByNumber(String accountNumber) {
        return ResponseEntity.ok(toDto(service.getByAccountNumber(accountNumber)));
    }

    @Override
    public ResponseEntity<AccountDto> depositByAccountNumber(String accountNumber, AmountDto body) {
        var updated = service.depositByNumber(accountNumber,
                new com.bank.accountms.api.dto.AccountDtos.AmountDto(body.getAmount())
        );
        return ResponseEntity.ok(toDto(updated));
    }

    @Override
    public ResponseEntity<AccountDto> withdrawByAccountNumber(String accountNumber, AmountDto body) {
        var updated = service.withdrawByNumber(accountNumber,
                new com.bank.accountms.api.dto.AccountDtos.AmountDto(body.getAmount())
        );
        return ResponseEntity.ok(toDto(updated));
    }


    // ---- helpers ----
    private AccountDto toDto(Account a) {
        AccountDto dto = new AccountDto();
        dto.setId(a.getId());
        dto.setAccountNumber(a.getAccountNumber());
        dto.setBalance(a.getBalance());
        dto.setCustomerId(a.getCustomerId());
        dto.setAccountType(AccountDto.AccountTypeEnum.fromValue(a.getAccountType().name()));
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
