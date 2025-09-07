package com.transactionms.service.impl;

import com.transactionms.client.AccountClient;
import com.transactionms.exceptions.AccountNotFoundException;
import com.transactionms.exceptions.InsufficientFundsException;
import com.transactionms.exceptions.InvalidTransactionException;
import com.transactionms.repository.TransactionRepository;
import com.transactionms.repository.model.Transaction;
import com.transactionms.repository.model.TransactionType;
import com.transactionms.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final AccountClient accountClient; // 👈 inyectamos el cliente

    @Override
    public Mono<Transaction> deposit(String accountNumber, Double amount) {
        if (amount <= 0) {
            return Mono.error(new InvalidTransactionException("El monto debe ser mayor a 0"));
        }

        // 1️⃣ validar que la cuenta exista en account-ms
        return accountClient.getByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("Cuenta destino no encontrada")))
                // 2️⃣ si existe, invocar depósito en account-ms
                .flatMap(account ->
                        accountClient.depositByNumberAccount(accountNumber, amount)
                                // 3️⃣ guardar la transacción en Mongo
                                .flatMap(updated -> {
                                    Transaction tx = Transaction.builder()
                                            .type(TransactionType.DEPOSIT)
                                            .accountTo(accountNumber)
                                            .amount(amount)
                                            .date(Instant.now())
                                            .build();
                                    return repository.save(tx);
                                })
                );
    }

    @Override
    public Mono<Transaction> withdraw(String accountNumber, Double amount) {
        if (amount <= 0) {
            return Mono.error(new InvalidTransactionException("El monto debe ser mayor a 0"));
        }

        return accountClient.getByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("Cuenta no encontrada")))
                .flatMap(account -> {
                    if (account.getBalance() < amount) {
                        return Mono.error(new InsufficientFundsException("Fondos insuficientes en la cuenta " + accountNumber));
                    }
                    return accountClient.withdrawByAccountNumber(accountNumber, amount)
                            .flatMap(updated -> {
                                Transaction tx = Transaction.builder()
                                        .type(TransactionType.WITHDRAW)
                                        .accountFrom(accountNumber)
                                        .amount(amount)
                                        .date(Instant.now())
                                        .build();
                                return repository.save(tx);
                            });
                });
    }

    @Override
    public Mono<Transaction> transfer(String originAccountNumber, String destinationAccountNumber, Double amount) {
        if (amount <= 0) {
            return Mono.error(new InvalidTransactionException("El monto debe ser mayor a 0"));
        }
        if (originAccountNumber.equals(destinationAccountNumber)) {
            return Mono.error(new InvalidTransactionException("La cuenta origen y destino no pueden ser iguales"));
        }

        return accountClient.getByAccountNumber(originAccountNumber)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("Cuenta origen no encontrada")))
                .flatMap(origin -> {
                    if (origin.getBalance() < amount) {
                        return Mono.error(new InsufficientFundsException("Fondos insuficientes en la cuenta" + originAccountNumber));
                    }
                    return accountClient.getByAccountNumber(destinationAccountNumber)
                            .switchIfEmpty(Mono.error(new AccountNotFoundException("Cuenta destino no encontrada")))
                            // 1️⃣ retirar de origen
                            .flatMap(dest ->
                                    accountClient.withdraw(origin.getId(), amount)
                                            // 2️⃣ depositar en destino
                                            .then(accountClient.deposit(dest.getId(), amount))
                                            // 3️⃣ guardar la transacción
                                            .flatMap(updated -> {
                                                Transaction tx = Transaction.builder()
                                                        .type(TransactionType.TRANSFER)
                                                        .accountFrom(originAccountNumber)
                                                        .accountTo(destinationAccountNumber)
                                                        .amount(amount)
                                                        .date(Instant.now())
                                                        .build();
                                                return repository.save(tx);
                                            })
                            );
                });
    }

    @Override
    public Flux<Transaction> history(String cuentaId, String tipo, LocalDate fechaDesde, LocalDate fechaHasta) {
        Flux<Transaction> baseFlux;

        if (cuentaId != null) {
            baseFlux = repository.findByAccountFromOrAccountToOrderByDateDesc(cuentaId, cuentaId);
        } else {
            baseFlux = repository.findAll();
        }

        return baseFlux
                .filter(tx -> tipo == null || tx.getType().name().equalsIgnoreCase(tipo))
                .filter(tx -> fechaDesde == null || !tx.getDate().isBefore(fechaDesde.atStartOfDay().toInstant(java.time.ZoneOffset.UTC)))
                .filter(tx -> fechaHasta == null || !tx.getDate().isAfter(fechaHasta.plusDays(1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC)));
    }
}
