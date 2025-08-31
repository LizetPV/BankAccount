package com.bank.accountms.service;

import com.bank.accountms.api.dto.AccountDtos.AccountCreateDto;
import com.bank.accountms.api.dto.AccountDtos.AmountDto;
import com.bank.accountms.domain.Account;
import com.bank.accountms.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AccountService {

    public static final double OVERDRAFT_LIMIT = -500.00; // checking
    private final AccountRepository repo;

    public Account create(AccountCreateDto dto) {
        var type = Account.AccountType.valueOf(dto.accountType().toUpperCase());
        if (dto.initialDeposit() <= 0) throw new IllegalArgumentException("Initial deposit must be > 0");

        var acc = Account.builder()
                .accountNumber(generateNumber())
                .balance(dto.initialDeposit())
                .accountType(type)
                .customerId(dto.customerId())
                .build();
        return repo.save(acc);
    }

    public List<Account> list(Long customerId) {
        return customerId == null ? repo.findAll() : repo.findByCustomerId(customerId);
    }

    public Account get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Account not found"));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Account deposit(Long id, AmountDto dto) {
        if (dto.amount() <= 0) throw new IllegalArgumentException("Amount must be > 0");
        var a = get(id);
        a.setBalance(a.getBalance() + dto.amount());
        return repo.save(a);
    }

    public Account withdraw(Long id, AmountDto dto) {
        if (dto.amount() <= 0) throw new IllegalArgumentException("Amount must be > 0");
        var a = get(id);
        double candidate = a.getBalance() - dto.amount();

        if (a.getAccountType() == Account.AccountType.SAVINGS) {
            if (candidate < 0) throw new IllegalStateException("Savings cannot be negative");
        } else { // CHECKING
            if (candidate < OVERDRAFT_LIMIT) throw new IllegalStateException("Checking overdraft limit exceeded (-500.00)");
        }
        a.setBalance(candidate);
        return repo.save(a);
    }

    // ejemplo async para total por cliente
    public CompletableFuture<Double> totalBalanceAsync(Long customerId) {
        var accounts = repo.findByCustomerId(customerId);
        var futures = accounts.stream()
                .map(a -> CompletableFuture.supplyAsync(a::getBalance))
                .toList();
        return CompletableFuture.supplyAsync(() ->
                futures.stream().mapToDouble(CompletableFuture::join).sum()
        );
    }

    private String generateNumber() {
        return "ACC" + System.nanoTime();
    }
}
