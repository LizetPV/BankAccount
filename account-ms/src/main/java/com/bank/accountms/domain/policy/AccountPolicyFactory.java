package com.bank.accountms.domain.policy;

import com.bank.accountms.domain.Account;

public final class AccountPolicyFactory {
    private AccountPolicyFactory() {}
    public static AccountPolicy forType(Account.AccountType type) {
        return switch (type) {
            case SAVINGS -> new SavingsPolicy();
            case CHECKING -> new CheckingPolicy();
        };
    }
}
