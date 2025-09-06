package com.transaction_ms.repository;

import com.transaction_ms.repository.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {
    // Transacciones donde una cuenta es origen
    Flux<Transaction> findByAccountFromOrderByDateDesc(String accountFrom);

    // Transacciones donde una cuenta es destino
    Flux<Transaction> findByAccountToOrderByDateDesc(String accountTo);

    // Historial completo (origen o destino)
    Flux<Transaction> findByAccountFromOrAccountToOrderByDateDesc(String accountFrom, String accountTo);
}
