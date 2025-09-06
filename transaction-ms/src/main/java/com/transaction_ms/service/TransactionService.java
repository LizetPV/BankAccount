package com.transaction_ms.service;

import com.transaction_ms.repository.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface TransactionService {
    Mono<Transaction> deposit(String accountNumber, Double amount);
    Mono<Transaction> withdraw(String accountNumber, Double amount);
    Mono<Transaction> transfer(String originAccount, String destinationAccount, Double amount);
    Flux<Transaction> history(String cuentaId, String tipo, LocalDate fechaDesde, LocalDate fechaHasta);
}
