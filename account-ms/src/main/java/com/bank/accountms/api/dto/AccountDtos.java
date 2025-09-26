package com.bank.accountms.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Clases DTO para operaciones sobre cuentas.
 */
public final class AccountDtos {

    /**
     * DTO de lectura de cuentas.
     *
     * @param id           identificador de la cuenta
     * @param accountNumber número de cuenta
     * @param balance       saldo actual
     * @param accountType   tipo de cuenta
     * @param customerId    identificador del cliente
     */
    public record AccountDto(
            Long id,
            String accountNumber,
            Double balance,
            String accountType,
            Long customerId
    ) { }

    /**
     * DTO para la creación de cuentas.
     */
    public record AccountCreateDto(
            @NotNull Long customerId,
            @NotBlank String accountType,
            @NotNull @Min(1) Double initialDeposit
    ) { }

    /**
     * DTO para operaciones de monto (depósito/retiro).
     */
    public record AmountDto(
            @NotNull @Min(1) Double amount
    ) { }

    private AccountDtos() { }
}
