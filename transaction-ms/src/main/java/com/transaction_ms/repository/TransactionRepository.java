package com.transaction_ms.repository;

import com.transaction_ms.repository.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {
    Flux<Transaction> findByAccountFromOrderByDateDesc(String accountFrom);
    Flux<Transaction> findByAccountToOrderByDateDesc(String accountTo);
    Flux<Transaction> findByAccountFromOrAccountToOrderByDateDesc(String accountFrom, String accountTo);
}
