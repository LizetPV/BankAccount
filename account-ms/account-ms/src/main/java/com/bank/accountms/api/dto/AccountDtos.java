package com.bank.accountms.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTOs como records (Java 17)
 */
public final class AccountDtos {

    public record AccountDto(
            Long id,
            String accountNumber,
            Double balance,
            String accountType, // "SAVINGS" | "CHECKING"
            Long customerId
    ) {}

    public record AccountCreateDto(
            @NotNull Long customerId,
            @NotBlank String accountType, // "SAVINGS" | "CHECKING"
            @NotNull @Min(1) Double initialDeposit
    ) {}

    public record AmountDto(
            @NotNull @Min(1) Double amount
    ) {}

    private AccountDtos() {}
}
