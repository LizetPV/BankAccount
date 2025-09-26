package com.bank.customerms.domain.rule;

@FunctionalInterface
public interface CustomerDeletionRule {
    void checkDeletable(Long customerId);
}