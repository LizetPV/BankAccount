package com.bank.accountms.domain.policy;

import com.bank.accountms.domain.Account;

/**
 * Strategy para reglas por tipo de cuenta (SRP/OCP).
 */
public interface AccountPolicy {
    default void validateDeposit(Account account, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");
    }
    void validateWithdrawal(Account account, double amount);
}
