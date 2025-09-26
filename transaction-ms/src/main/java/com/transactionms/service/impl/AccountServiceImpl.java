package com.transactionms.service.impl;

import com.transactionms.client.account.api.CuentasApi;
import com.transactionms.client.account.dto.AccountDto;
import com.transactionms.client.account.dto.AmountDto;
import com.transactionms.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementación del servicio de cuentas que consume la API externa de cuentas.
 * Cumple con Dependency Inversion Principle (DIP) - implementa la abstracción AccountService.
 * Utiliza el cliente generado por OpenAPI para comunicarse con el microservicio de cuentas.
 * 
 * Esta implementación reemplaza a AccountClient usando el cliente generado automáticamente.
 */
@Slf4j
@Service("openApiAccountService")
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final CuentasApi cuentasApi;

    @Override
    public Mono<com.transactionms.client.dto.AccountDto> getAccount(Long id) {
        log.debug("Obteniendo cuenta por ID: {}", id);
        return Mono.fromCallable(() -> cuentasApi.getAccount(id))
                .map(this::convertToInternalDto)
                .doOnSuccess(account -> log.debug("Cuenta obtenida: {}", account.getAccountNumber()))
                .doOnError(error -> log.error("Error al obtener cuenta por ID {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<com.transactionms.client.dto.AccountDto> getByAccountNumber(String accountNumber) {
        log.debug("Obteniendo cuenta por número: {}", accountNumber);
        return Mono.fromCallable(() -> cuentasApi.getAccountByNumber(accountNumber))
                .map(this::convertToInternalDto)
                .doOnSuccess(account -> log.debug("Cuenta obtenida por número: {}", accountNumber))
                .doOnError(error -> log.error("Error al obtener cuenta por número {}: {}", accountNumber, error.getMessage()));
    }

    @Override
    public Mono<com.transactionms.client.dto.AccountDto> deposit(Long id, Double amount) {
        log.debug("Realizando depósito de {} en cuenta ID: {}", amount, id);
        AmountDto amountDto = new AmountDto();
        amountDto.setAmount(amount);
        
        return Mono.fromCallable(() -> cuentasApi.deposit(id, amountDto))
                .map(this::convertToInternalDto)
                .doOnSuccess(account -> log.debug("Depósito realizado exitosamente. Nuevo saldo: {}", account.getBalance()))
                .doOnError(error -> log.error("Error al realizar depósito de {} en cuenta {}: {}", amount, id, error.getMessage()));
    }

    @Override
    public Mono<com.transactionms.client.dto.AccountDto> depositByAccountNumber(String accountNumber, Double amount) {
        log.debug("Realizando depósito de {} en cuenta número: {}", amount, accountNumber);
        AmountDto amountDto = new AmountDto();
        amountDto.setAmount(amount);
        
        return Mono.fromCallable(() -> cuentasApi.depositByAccountNumber(accountNumber, amountDto))
                .map(this::convertToInternalDto)
                .doOnSuccess(account -> log.debug("Depósito por número realizado exitosamente. Nuevo saldo: {}", account.getBalance()))
                .doOnError(error -> log.error("Error al realizar depósito de {} en cuenta {}: {}", amount, accountNumber, error.getMessage()));
    }

    @Override
    public Mono<com.transactionms.client.dto.AccountDto> withdraw(Long id, Double amount) {
        log.debug("Realizando retiro de {} en cuenta ID: {}", amount, id);
        AmountDto amountDto = new AmountDto();
        amountDto.setAmount(amount);
        
        return Mono.fromCallable(() -> cuentasApi.withdraw(id, amountDto))
                .map(this::convertToInternalDto)
                .doOnSuccess(account -> log.debug("Retiro realizado exitosamente. Nuevo saldo: {}", account.getBalance()))
                .doOnError(error -> log.error("Error al realizar retiro de {} en cuenta {}: {}", amount, id, error.getMessage()));
    }

    @Override
    public Mono<com.transactionms.client.dto.AccountDto> withdrawByAccountNumber(String accountNumber, Double amount) {
        log.debug("Realizando retiro de {} en cuenta número: {}", amount, accountNumber);
        AmountDto amountDto = new AmountDto();
        amountDto.setAmount(amount);
        
        return Mono.fromCallable(() -> cuentasApi.withdrawByAccountNumber(accountNumber, amountDto))
                .map(this::convertToInternalDto)
                .doOnSuccess(account -> log.debug("Retiro por número realizado exitosamente. Nuevo saldo: {}", account.getBalance()))
                .doOnError(error -> log.error("Error al realizar retiro de {} en cuenta {}: {}", amount, accountNumber, error.getMessage()));
    }

    /**
     * Convierte el DTO del cliente generado al DTO interno del módulo.
     * Esto permite mantener la separación entre el contrato externo y el modelo interno.
     * 
     * @param externalDto DTO del cliente generado por OpenAPI
     * @return DTO interno del módulo de transacciones
     */
    private com.transactionms.client.dto.AccountDto convertToInternalDto(AccountDto externalDto) {
        com.transactionms.client.dto.AccountDto internalDto = new com.transactionms.client.dto.AccountDto();
        internalDto.setId(externalDto.getId());
        internalDto.setAccountNumber(externalDto.getAccountNumber());
        internalDto.setBalance(externalDto.getBalance());
        internalDto.setAccountType(externalDto.getAccountType() != null ? externalDto.getAccountType().getValue() : null);
        internalDto.setCustomerId(externalDto.getCustomerId());
        return internalDto;
    }
}