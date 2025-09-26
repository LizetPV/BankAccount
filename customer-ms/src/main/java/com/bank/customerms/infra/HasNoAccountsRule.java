package com.bank.customerms.infra;

import com.bank.customerms.client.AccountClient;
import com.bank.customerms.domain.rule.CustomerDeletionRule;
import org.springframework.stereotype.Component;

@Component
public class HasNoAccountsRule implements CustomerDeletionRule {

    private final AccountClient accountClient;

    public HasNoAccountsRule(AccountClient accountClient) {
        this.accountClient = accountClient;
    }

    @Override
    public void checkDeletable(Long customerId) {
        if (accountClient.hasAccounts(customerId)) {
            throw new IllegalStateException("Customer has active accounts");
        }
    }
}