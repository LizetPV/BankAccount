package com.transactionms.factory;

import com.transactionms.repository.model.Transaction;
import com.transactionms.repository.model.TransactionType;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

/**
 * Factory para la construcción de entidades Transaction.
 * Cumple con Single Responsibility Principle (SRP) - solo se encarga de crear transacciones.
 * Mejora la testabilidad al permitir inyectar Clock para controlar tiempo en tests.
 */
@Component
public class TransactionFactory {

    private final Clock clock;

    // Constructor que permite inyectar Clock para tests
    public TransactionFactory(Clock clock) {
        this.clock = clock;
    }

    // Constructor por defecto para uso normal
    public TransactionFactory() {
        this.clock = Clock.systemUTC();
    }

    /**
     * Crea una transacción de depósito.
     * @param accountTo cuenta destino
     * @param amount monto del depósito
     * @return transacción de depósito
     */
    public Transaction createDeposit(String accountTo, Double amount) {
        return Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .accountTo(accountTo)
                .amount(amount)
                .date(getCurrentInstant())
                .build();
    }

    /**
     * Crea una transacción de depósito con notas.
     * @param accountTo cuenta destino
     * @param amount monto del depósito
     * @param notes notas adicionales
     * @return transacción de depósito
     */
    public Transaction createDeposit(String accountTo, Double amount, String notes) {
        return Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .accountTo(accountTo)
                .amount(amount)
                .date(getCurrentInstant())
                .notes(notes)
                .build();
    }

    /**
     * Crea una transacción de retiro.
     * @param accountFrom cuenta origen
     * @param amount monto del retiro
     * @return transacción de retiro
     */
    public Transaction createWithdraw(String accountFrom, Double amount) {
        return Transaction.builder()
                .type(TransactionType.WITHDRAW)
                .accountFrom(accountFrom)
                .amount(amount)
                .date(getCurrentInstant())
                .build();
    }

    /**
     * Crea una transacción de retiro con notas.
     * @param accountFrom cuenta origen
     * @param amount monto del retiro
     * @param notes notas adicionales
     * @return transacción de retiro
     */
    public Transaction createWithdraw(String accountFrom, Double amount, String notes) {
        return Transaction.builder()
                .type(TransactionType.WITHDRAW)
                .accountFrom(accountFrom)
                .amount(amount)
                .date(getCurrentInstant())
                .notes(notes)
                .build();
    }

    /**
     * Crea una transacción de transferencia.
     * @param accountFrom cuenta origen
     * @param accountTo cuenta destino
     * @param amount monto de la transferencia
     * @return transacción de transferencia
     */
    public Transaction createTransfer(String accountFrom, String accountTo, Double amount) {
        return Transaction.builder()
                .type(TransactionType.TRANSFER)
                .accountFrom(accountFrom)
                .accountTo(accountTo)
                .amount(amount)
                .date(getCurrentInstant())
                .build();
    }

    /**
     * Crea una transacción de transferencia con notas.
     * @param accountFrom cuenta origen
     * @param accountTo cuenta destino
     * @param amount monto de la transferencia
     * @param notes notas adicionales
     * @return transacción de transferencia
     */
    public Transaction createTransfer(
        String accountFrom,
        String accountTo,
        Double amount,
        String notes) {
        return Transaction.builder()
                .type(TransactionType.TRANSFER)
                .accountFrom(accountFrom)
                .accountTo(accountTo)
                .amount(amount)
                .date(getCurrentInstant())
                .notes(notes)
                .build();
    }

    /**
     * Obtiene el tiempo actual usando el Clock inyectado.
     * Esto hace que la clase sea testeable al permitir mockar el tiempo.
     * @return instante actual
     */
    private Instant getCurrentInstant() {
        return clock.instant();
    }
}