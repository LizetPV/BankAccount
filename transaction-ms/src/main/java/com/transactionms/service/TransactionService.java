package com.transactionms.service;

import com.transactionms.repository.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface TransactionService {
    Mono<Transaction> deposit(String accountId, Double amount);
    Mono<Transaction> withdraw(String accountId, Double amount);
    Mono<Transaction> transfer(String originId, String destinationId, Double amount);
    Flux<Transaction> history(String cuentaId, String tipo, LocalDate fechaDesde, LocalDate fechaHasta);
}
