package com.transaction_ms.service.impl;

import com.transaction_ms.exceptions.AccountNotFoundException;
import com.transaction_ms.exceptions.InsufficientFundsException;
import com.transaction_ms.exceptions.InvalidTransactionException;
import com.transaction_ms.repository.TransactionRepository;
import com.transaction_ms.repository.model.Transaction;
import com.transaction_ms.repository.model.TransactionType;
import com.transaction_ms.service.TransactionService;
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

    @Override
    public Mono<Transaction> deposit(String accountNumber, Double amount) {
        if (amount <= 0) {
            return Mono.error(new InvalidTransactionException("El monto debe ser mayor a 0"));
        }

        // ⚠️ Aquí podrías validar que la cuenta exista (consultando el microservicio de cuentas)
        if (accountNumber == null || accountNumber.isBlank()) {
            return Mono.error(new AccountNotFoundException("Cuenta destino no encontrada"));
        }

        Transaction tx = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .accountTo(accountNumber)
                .amount(amount)
                .date(Instant.now())
                .build();

        return repository.save(tx);
    }

    @Override
    public Mono<Transaction> withdraw(String accountNumber, Double amount) {
        if (amount <= 0) {
            return Mono.error(new InvalidTransactionException("El monto debe ser mayor a 0"));
        }

        if (accountNumber == null || accountNumber.isBlank()) {
            return Mono.error(new AccountNotFoundException("Cuenta no encontrada"));
        }

        // ⚠️ Aquí deberías validar saldo en el microservicio de cuentas
        // Ejemplo "mock" (como placeholder):
        double saldoActual = 100.0; // Esto debería venir de otro microservicio
        if (amount > saldoActual) {
            return Mono.error(new InsufficientFundsException("Fondos insuficientes en la cuenta " + accountNumber));
        }

        Transaction tx = Transaction.builder()
                .type(TransactionType.WITHDRAW)
                .accountFrom(accountNumber)
                .amount(amount)
                .date(Instant.now())
                .build();

        return repository.save(tx);
    }

    @Override
    public Mono<Transaction> transfer(String originAccount, String destinationAccount, Double amount) {
        if (amount <= 0) {
            return Mono.error(new InvalidTransactionException("El monto debe ser mayor a 0"));
        }

        if (originAccount == null || destinationAccount == null) {
            return Mono.error(new AccountNotFoundException("Cuenta origen o destino no encontrada"));
        }

        if (originAccount.equals(destinationAccount)) {
            return Mono.error(new InvalidTransactionException("La cuenta origen y destino no pueden ser iguales"));
        }

        // ⚠️ Aquí deberías validar saldo en el microservicio de cuentas
        double saldoActual = 50.0; // Mock temporal
        if (amount > saldoActual) {
            return Mono.error(new InsufficientFundsException("Fondos insuficientes en la cuenta " + originAccount));
        }

        Transaction tx = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .accountFrom(originAccount)
                .accountTo(destinationAccount)
                .amount(amount)
                .date(Instant.now())
                .build();

        return repository.save(tx);
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
