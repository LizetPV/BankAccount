package com.bank.repository.memory;

import com.bank.domain.BankAccount;
import com.bank.repository.BankAccountRepository;


import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


public class InMemoryBankAccountRepository implements BankAccountRepository {
    private final Map<String, BankAccount> byNumber = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(100_000L);


    private String nextNumber() { return String.format("ACC%08d", seq.incrementAndGet()); }


    @Override
    public BankAccount save(BankAccount account) {
        if (account.getAccountNumber() == null || account.getAccountNumber().isBlank()) {
            account.setAccountNumber(nextNumber());
        }
        byNumber.put(account.getAccountNumber(), account);
        return account;
    }


    @Override
    public Optional<BankAccount> findByNumber(String accountNumber) {
        return Optional.ofNullable(byNumber.get(accountNumber));
    }
}