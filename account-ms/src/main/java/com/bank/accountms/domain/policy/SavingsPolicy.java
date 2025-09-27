package com.bank.accountms.domain.policy;

import com.bank.accountms.domain.Account;

public class SavingsPolicy implements AccountPolicy {
    @Override
    public void validateWithdrawal(Account account, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");
        double candidate = account.getBalance() - amount;
        if (candidate < 0) throw new IllegalStateException("Savings cannot be negative");
    }
}
