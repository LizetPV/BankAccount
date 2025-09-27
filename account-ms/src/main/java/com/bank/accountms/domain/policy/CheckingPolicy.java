package com.bank.accountms.domain.policy;

import com.bank.accountms.domain.Account;

public class CheckingPolicy implements AccountPolicy {
    public static final double OVERDRAFT_LIMIT = -500.00;
    @Override
    public void validateWithdrawal(Account account, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");
        double candidate = account.getBalance() - amount;
        if (candidate < OVERDRAFT_LIMIT)
            throw new IllegalStateException("Checking overdraft limit exceeded (-500.00)");
    }
}
