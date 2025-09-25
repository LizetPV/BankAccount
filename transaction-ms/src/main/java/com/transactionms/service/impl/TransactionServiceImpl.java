package com.transactionms.service.impl;

import com.transactionms.service.AccountService;
import com.transactionms.exceptions.AccountNotFoundException;
import com.transactionms.exceptions.InsufficientFundsException;
import com.transactionms.factory.TransactionFactory;
import com.transactionms.repository.TransactionRepository;
import com.transactionms.repository.model.Transaction;
import com.transactionms.service.TransactionService;
import com.transactionms.validator.TransactionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Implementación del servicio de transacciones.
 * Refactorizado para cumplir mejor con los principios SOLID:
 * - SRP: Delegación de responsabilidades a TransactionValidator y TransactionFactory
 * - DIP: Depende de AccountService (abstracción) en lugar de AccountClient (implementación)
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final AccountService accountService; // ✅ DIP: Depende de abstracción
    private final TransactionValidator validator; // ✅ SRP: Validaciones delegadas
    private final TransactionFactory transactionFactory; // ✅ SRP: Construcción delegada

    @Override
    public Mono<Transaction> deposit(String accountNumber, Double amount) {
        // ✅ SRP: Validación delegada a TransactionValidator
        validator.validateDeposit(accountNumber, amount);

        // 1️⃣ validar que la cuenta exista en account-ms
        return accountService.getByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(
                    new AccountNotFoundException("Cuenta destino no encontrada")))
                // 2️⃣ si existe, invocar depósito en account-ms
                .flatMap(account ->
                        accountService.depositByAccountNumber(accountNumber, amount)
                                // 3️⃣ guardar la transacción usando el factory
                                .flatMap(updated -> {
                                    // ✅ SRP: Construcción delegada a TransactionFactory
                                    Transaction tx = transactionFactory.createDeposit(accountNumber, amount);
                                    return repository.save(tx);
                                })
                );
    }

    @Override
    public Mono<Transaction> withdraw(String accountNumber, Double amount) {
        // ✅ SRP: Validación delegada a TransactionValidator
        validator.validateWithdraw(accountNumber, amount);

        return accountService.getByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("Cuenta no encontrada")))
                .flatMap(account -> {
                    if (account.getBalance() < amount) {
                        return Mono.error(new InsufficientFundsException(
                            "Fondos insuficientes en la cuenta " + accountNumber));
                    }
                    return accountService.withdrawByAccountNumber(accountNumber, amount)
                            .flatMap(updated -> {
                                // ✅ SRP: Construcción delegada a TransactionFactory
                                Transaction tx = transactionFactory.createWithdraw(accountNumber, amount);
                                return repository.save(tx);
                            });
                });
    }

    @Override
    public Mono<Transaction> transfer(String originAccountNumber,
        String destinationAccountNumber, Double amount) {
        // ✅ SRP: Validación delegada a TransactionValidator
        validator.validateTransfer(originAccountNumber, destinationAccountNumber, amount);

        return accountService.getByAccountNumber(originAccountNumber)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(
                    "Cuenta origen no encontrada")))
                .flatMap(origin -> {
                    if (origin.getBalance() < amount) {
                        return Mono.error(new InsufficientFundsException(
                            "Fondos insuficientes en la cuenta" + originAccountNumber));
                    }
                    return accountService.getByAccountNumber(destinationAccountNumber)
                            .switchIfEmpty(Mono.error(new AccountNotFoundException(
                                "Cuenta destino no encontrada")))
                            // 1️⃣ retirar de origen
                            .flatMap(dest ->
                                    accountService.withdraw(origin.getId(), amount)
                                            // 2️⃣ depositar en destino
                                            .then(accountService.deposit(dest.getId(), amount))
                                            // 3️⃣ guardar la transacción usando el factory
                                            .flatMap(updated -> {
                                                // ✅ SRP: Construcción delegada a TransactionFactory
                                                Transaction tx = transactionFactory.createTransfer(
                                                    originAccountNumber, destinationAccountNumber, amount);
                                                return repository.save(tx);
                                            })
                            );
                });
    }

    @Override
    public Flux<Transaction> history(
        String cuentaNumero, String tipo, LocalDate fechaDesde, LocalDate fechaHasta) {
        Flux<Transaction> baseFlux;

        if (cuentaNumero != null) {
            baseFlux = repository.findByAccountFromOrAccountToOrderByDateDesc(
                cuentaNumero,cuentaNumero);
        } else {
            baseFlux = repository.findAll();
        }

        return baseFlux
                .filter(tx -> tipo == null || tx.getType().name().equalsIgnoreCase(tipo))
                // desde inclusivo: >= 00:00 de fechaDesde
                .filter(tx -> fechaDesde == null
                        || !tx.getDate().isBefore(
                            fechaDesde.atStartOfDay().toInstant(ZoneOffset.UTC)))
                // hasta inclusivo (fin de día): <= (fechaHasta + 1 día) 00:00
                .filter(tx -> fechaHasta == null
                        || !tx.getDate().isAfter(
                            fechaHasta.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)));
    }
}
