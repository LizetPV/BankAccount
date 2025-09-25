package com.transactionms.service;

import com.transactionms.client.dto.AccountDto;
import reactor.core.publisher.Mono;

/**
 * Interface para operaciones con cuentas bancarias.
 * Cumple con Dependency Inversion Principle (DIP) - abstracción en lugar de implementación concreta.
 * Cumple con Interface Segregation Principle (ISP) - interface específica para operaciones de cuenta.
 */
public interface AccountService {

    /**
     * Obtiene una cuenta por su ID.
     * @param id identificador de la cuenta
     * @return cuenta encontrada
     */
    Mono<AccountDto> getAccount(Long id);

    /**
     * Obtiene una cuenta por su número.
     * @param accountNumber número de la cuenta
     * @return cuenta encontrada
     */
    Mono<AccountDto> getByAccountNumber(String accountNumber);

    /**
     * Realiza un depósito en una cuenta por ID.
     * @param id identificador de la cuenta
     * @param amount monto a depositar
     * @return cuenta actualizada
     */
    Mono<AccountDto> deposit(Long id, Double amount);

    /**
     * Realiza un depósito en una cuenta por número.
     * @param accountNumber número de la cuenta
     * @param amount monto a depositar
     * @return cuenta actualizada
     */
    Mono<AccountDto> depositByAccountNumber(String accountNumber, Double amount);

    /**
     * Realiza un retiro en una cuenta por ID.
     * @param id identificador de la cuenta
     * @param amount monto a retirar
     * @return cuenta actualizada
     */
    Mono<AccountDto> withdraw(Long id, Double amount);

    /**
     * Realiza un retiro en una cuenta por número.
     * @param accountNumber número de la cuenta
     * @param amount monto a retirar
     * @return cuenta actualizada
     */
    Mono<AccountDto> withdrawByAccountNumber(String accountNumber, Double amount);
}