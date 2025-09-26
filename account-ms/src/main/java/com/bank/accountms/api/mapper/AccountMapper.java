package com.bank.accountms.api.mapper;

import com.bank.accountms.api.dto.AccountDtos.AccountDto;
import com.bank.accountms.domain.Account;

/**
 * Utilidad para mapear entidades Account a DTOs.
 */
public final class AccountMapper {

    private AccountMapper() { }

    public static AccountDto toDto(Account a) {
        return new AccountDto(
                a.getId(),
                a.getAccountNumber(),
                a.getBalance(),
                a.getAccountType().name(),
                a.getCustomerId()
        );
    }
}
