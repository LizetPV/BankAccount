package com.transactionms.service.impl;

import com.transactionms.service.AccountService;
import com.transactionms.exceptions.AccountNotFoundException;
import com.transactionms.exceptions.InsufficientFundsException;
import com.transactionms.factory.TransactionFactory;
import com.transactionms.repository.TransactionRepository;
import com.transactionms.repository.model.Transaction;
import com.transactionms.service.TransactionService;
import com.transactionms.validator.TransactionValidator;
import com.transactionms.exceptions.InvalidTransactionException;
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
        return Mono.defer(() -> {
            try {
                validator.validateDeposit(accountNumber, amount);
            } catch (InvalidTransactionException e) {
                return Mono.error(e); // Propaga la excepción reactivamente
            }

            // Continúa la lógica original si la validación es exitosa
            return accountService.getByAccountNumber(accountNumber)
                    .switchIfEmpty(Mono.error(
                            new AccountNotFoundException("Cuenta destino no encontrada")))
                    .flatMap(account ->
                            accountService.depositByAccountNumber(accountNumber, amount)
                                    .flatMap(updated -> {
                                        Transaction tx = transactionFactory.createDeposit(accountNumber, amount);
                                        return repository.save(tx);
                                    })
                    );
        });
    }

    @Override
    public Mono<Transaction> withdraw(String accountNumber, Double amount) {
        return Mono.defer(() -> {
            try {
                validator.validateWithdraw(accountNumber, amount);
            } catch (InvalidTransactionException e) {
                return Mono.error(e);
            }

            // Continúa la lógica original si la validación es exitosa
            return accountService.getByAccountNumber(accountNumber)
                    .switchIfEmpty(Mono.error(new AccountNotFoundException("Cuenta no encontrada")))
                    .flatMap(account -> {
                        if (account.getBalance() < amount) {
                            return Mono.error(new InsufficientFundsException(
                                    "Fondos insuficientes en la cuenta " + accountNumber));
                        }
                        return accountService.withdrawByAccountNumber(accountNumber, amount)
                                .flatMap(updated -> {
                                    Transaction tx = transactionFactory.createWithdraw(accountNumber, amount);
                                    return repository.save(tx);
                                });
                    });
        });
    }

    @Override
    public Mono<Transaction> transfer(String originAccountNumber,
                                      String destinationAccountNumber, Double amount) {
        return Mono.defer(() -> {
            try {
                validator.validateTransfer(originAccountNumber, destinationAccountNumber, amount);
            } catch (InvalidTransactionException e) {
                return Mono.error(e);
            }

            // Continúa la lógica original si la validación es exitosa
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
                                .flatMap(dest ->
                                        accountService.withdraw(origin.getId(), amount)
                                                .then(accountService.deposit(dest.getId(), amount))
                                                .flatMap(updated -> {
                                                    Transaction tx = transactionFactory.createTransfer(
                                                            originAccountNumber, destinationAccountNumber, amount);
                                                    return repository.save(tx);
                                                })
                                );
                    });
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
