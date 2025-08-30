package com.bank.repository;

import com.bank.domain.BankAccount;
import java.util.*;


public interface BankAccountRepository {
    BankAccount save(BankAccount account);          // asigna número si falta
    Optional<BankAccount> findByNumber(String accountNumber);
}