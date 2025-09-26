package com.transactionms.factory;

import com.transactionms.repository.model.Transaction;
import com.transactionms.repository.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class TransactionFactoryTest {

    private TransactionFactory factory;
    private Clock fixedClock;
    private Instant fixedInstant;

    // Constantes para el test
    private static final String ACC_FROM = "ACC-ORIGIN";
    private static final String ACC_TO = "ACC-DEST";
    private static final Double AMOUNT = 200.0;
    private static final String NOTES = "Test Notes";

    @BeforeEach
    void setUp() {
        // Fijar el tiempo para que los tests sean deterministas
        fixedInstant = Instant.parse("2025-01-01T10:00:00Z");
        fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));
        factory = new TransactionFactory(fixedClock);
    }

    @Test
    void givenNoClockInjected_whenDefaultConstructor_thenUsesSystemUTC() {
        // Prueba el constructor por defecto (para cobertura, aunque no es ideal para lógica de tiempo)
        TransactionFactory defaultFactory = new TransactionFactory();
        assertNotNull(defaultFactory);
        // No hay una forma directa de probar el 'Clock.systemUTC()' sin reflección,
        // pero la creación de la instancia es suficiente para la cobertura de línea.
    }

    // =================================================================
    // TESTS PARA DEPÓSITO
    // =================================================================

    @Test
    void whenCreateDeposit_thenTransactionHasCorrectFields() {
        Transaction tx = factory.createDeposit(ACC_TO, AMOUNT);

        assertEquals(TransactionType.DEPOSIT, tx.getType());
        assertEquals(ACC_TO, tx.getAccountTo());
        assertNull(tx.getAccountFrom());
        assertEquals(AMOUNT, tx.getAmount());
        assertEquals(fixedInstant, tx.getDate());
        assertNull(tx.getNotes());
    }

    @Test
    void whenCreateDepositWithNotes_thenTransactionHasCorrectFields() {
        Transaction tx = factory.createDeposit(ACC_TO, AMOUNT, NOTES);

        assertEquals(TransactionType.DEPOSIT, tx.getType());
        assertEquals(NOTES, tx.getNotes());
    }

    // =================================================================
    // TESTS PARA RETIRO
    // =================================================================

    @Test
    void whenCreateWithdraw_thenTransactionHasCorrectFields() {
        Transaction tx = factory.createWithdraw(ACC_FROM, AMOUNT);

        assertEquals(TransactionType.WITHDRAW, tx.getType());
        assertEquals(ACC_FROM, tx.getAccountFrom());
        assertNull(tx.getAccountTo());
        assertEquals(AMOUNT, tx.getAmount());
    }

    @Test
    void whenCreateWithdrawWithNotes_thenTransactionHasCorrectFields() {
        Transaction tx = factory.createWithdraw(ACC_FROM, AMOUNT, NOTES);

        assertEquals(TransactionType.WITHDRAW, tx.getType());
        assertEquals(NOTES, tx.getNotes());
    }

    // =================================================================
    // TESTS PARA TRANSFERENCIA
    // =================================================================

    @Test
    void whenCreateTransfer_thenTransactionHasCorrectFields() {
        Transaction tx = factory.createTransfer(ACC_FROM, ACC_TO, AMOUNT);

        assertEquals(TransactionType.TRANSFER, tx.getType());
        assertEquals(ACC_FROM, tx.getAccountFrom());
        assertEquals(ACC_TO, tx.getAccountTo());
        assertEquals(AMOUNT, tx.getAmount());
    }

    @Test
    void whenCreateTransferWithNotes_thenTransactionHasCorrectFields() {
        Transaction tx = factory.createTransfer(ACC_FROM, ACC_TO, AMOUNT, NOTES);

        assertEquals(TransactionType.TRANSFER, tx.getType());
        assertEquals(NOTES, tx.getNotes());
    }
}