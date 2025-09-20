package com.transactionms.service;

import com.transactionms.client.AccountClient;
import com.transactionms.client.dto.AccountDto;
import com.transactionms.exceptions.InsufficientFundsException;
import com.transactionms.exceptions.InvalidTransactionException;
import com.transactionms.repository.TransactionRepository;
import com.transactionms.repository.model.Transaction;
import com.transactionms.repository.model.TransactionType;
import com.transactionms.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests con AAA, principios FIRST y YAGNI/KISS/DRY.
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    // ====== Constantes de prueba (DRY) ======
    private static final String ACC_1 = "ACC-001";
    private static final String ACC_2 = "ACC-002";
    private static final String ACC_3 = "ACC-003";
    private static final String ACC_ORIGIN = "ACC-010";
    private static final String ACC_DEST   = "ACC-999";
    private static final Double AMOUNT_100 = 100.0;
    private static final Double AMOUNT_150 = 150.0;
    private static final Double AMOUNT_200 = 200.0;
    private static final Double BAL_1000   = 1000.0;

    @Mock private TransactionRepository repository;
    @Mock private AccountClient accountClient;
    @InjectMocks private TransactionServiceImpl service;

    @BeforeEach
    void resetMocks() {
        reset(repository, accountClient);
    }

    // ====== Helpers ======
    private static AccountDto accDto(Long id, String number, double balance) {
        AccountDto a = new AccountDto();
        a.setId(id);
        a.setAccountNumber(number);
        a.setBalance(balance);
        a.setAccountType("SAVINGS");
        a.setCustomerId(10L);
        return a;
    }

    // ====== DEPÓSITO ======

    @Test
    void givenValidAmount_whenDeposit_thenSavesDepositTransaction() {
        // Arrange
        when(accountClient.getByAccountNumber(ACC_1))
                .thenReturn(Mono.just(accDto(1L, ACC_1, BAL_1000)));
        when(accountClient.depositByNumberAccount(ACC_1, AMOUNT_150))
                .thenReturn(Mono.just(accDto(1L, ACC_1, BAL_1000 + AMOUNT_150)));
        when(repository.save(any(Transaction.class)))
                .thenAnswer(inv -> {
                    Transaction t = inv.getArgument(0);
                    t.setId("tx-1");
                    return Mono.just(t);
                });

        // Act
        Mono<Transaction> result = service.deposit(ACC_1, AMOUNT_150);

        // Assert
        StepVerifier.create(result)
                .assertNext(tx -> {
                    org.junit.jupiter.api.Assertions.assertEquals(TransactionType.DEPOSIT, tx.getType());
                    org.junit.jupiter.api.Assertions.assertEquals(AMOUNT_150, tx.getAmount());
                    org.junit.jupiter.api.Assertions.assertNull(tx.getAccountFrom());
                    org.junit.jupiter.api.Assertions.assertEquals(ACC_1, tx.getAccountTo());
                    org.junit.jupiter.api.Assertions.assertNotNull(tx.getDate());
                })
                .verifyComplete();

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(repository).save(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(TransactionType.DEPOSIT, captor.getValue().getType());
        verify(accountClient).getByAccountNumber(ACC_1);
        verify(accountClient).depositByNumberAccount(ACC_1, AMOUNT_150);
    }

    @Test
    void givenInvalidAmount_whenDeposit_thenInvalidTransaction() {
        // Act & Assert
        StepVerifier.create(service.deposit(ACC_1, 0.0))
                .expectError(InvalidTransactionException.class)
                .verify();
        StepVerifier.create(service.deposit(ACC_1, -10.0))
                .expectError(InvalidTransactionException.class)
                .verify();

        verifyNoInteractions(accountClient);
        verify(repository, never()).save(any());
    }

    // ====== RETIRO ======

    @Test
    void givenBalanceEnough_whenWithdraw_thenSavesWithdrawTransaction() {
        // Arrange
        when(accountClient.getByAccountNumber(ACC_2))
                .thenReturn(Mono.just(accDto(2L, ACC_2, BAL_1000)));
        when(accountClient.withdrawByAccountNumber(ACC_2, AMOUNT_100))
                .thenReturn(Mono.just(accDto(2L, ACC_2, BAL_1000 - AMOUNT_100)));
        when(repository.save(any(Transaction.class)))
                .thenAnswer(inv -> {
                    Transaction t = inv.getArgument(0);
                    t.setId("tx-2");
                    return Mono.just(t);
                });

        // Act
        Mono<Transaction> result = service.withdraw(ACC_2, AMOUNT_100);

        // Assert
        StepVerifier.create(result)
                .assertNext(tx -> {
                    org.junit.jupiter.api.Assertions.assertEquals(TransactionType.WITHDRAW, tx.getType());
                    org.junit.jupiter.api.Assertions.assertEquals(AMOUNT_100, tx.getAmount());
                    org.junit.jupiter.api.Assertions.assertEquals(ACC_2, tx.getAccountFrom());
                    org.junit.jupiter.api.Assertions.assertNull(tx.getAccountTo());
                })
                .verifyComplete();

        verify(accountClient).getByAccountNumber(ACC_2);
        verify(accountClient).withdrawByAccountNumber(ACC_2, AMOUNT_100);
        verify(repository).save(any(Transaction.class));
    }

    @Test
    void givenInsufficientBalance_whenWithdraw_thenPropagatesInsufficientFunds() {
        // Arrange
        when(accountClient.getByAccountNumber(ACC_3))
                .thenReturn(Mono.just(accDto(3L, ACC_3, BAL_1000)));

        // Act & Assert
        StepVerifier.create(service.withdraw(ACC_3, 5000.0))
                .expectError(InsufficientFundsException.class)
                .verify();

        verify(repository, never()).save(any());
        verify(accountClient, never()).withdrawByAccountNumber(anyString(), anyDouble());
    }

    // ÚNICO: monto inválido en withdraw (antes había duplicados)
    @Test
    void givenInvalidAmount_whenWithdraw_thenInvalidTransaction() {
        StepVerifier.create(service.withdraw(ACC_2, 0.0))
                .expectError(InvalidTransactionException.class)
                .verify();

        StepVerifier.create(service.withdraw(ACC_2, -5.0))
                .expectError(InvalidTransactionException.class)
                .verify();

        verifyNoInteractions(accountClient);
        verify(repository, never()).save(any());
    }

    // ====== TRANSFERENCIA ======

    @Test
    void givenValidAccounts_whenTransfer_thenWithdrawOriginDepositDestAndSave() {
        // Arrange
        when(accountClient.getByAccountNumber(ACC_ORIGIN))
                .thenReturn(Mono.just(accDto(10L, ACC_ORIGIN, BAL_1000)));
        when(accountClient.getByAccountNumber(ACC_DEST))
                .thenReturn(Mono.just(accDto(99L, ACC_DEST, BAL_1000)));

        when(accountClient.withdraw(10L, AMOUNT_200))
                .thenReturn(Mono.just(accDto(10L, ACC_ORIGIN, BAL_1000 - AMOUNT_200)));
        when(accountClient.deposit(99L, AMOUNT_200))
                .thenReturn(Mono.just(accDto(99L, ACC_DEST, BAL_1000 + AMOUNT_200)));

        when(repository.save(any(Transaction.class)))
                .thenAnswer(inv -> {
                    Transaction t = inv.getArgument(0);
                    t.setId("tx-3");
                    return Mono.just(t);
                });

        // Act
        Mono<Transaction> result = service.transfer(ACC_ORIGIN, ACC_DEST, AMOUNT_200);

        // Assert
        StepVerifier.create(result)
                .assertNext(tx -> {
                    org.junit.jupiter.api.Assertions.assertEquals(TransactionType.TRANSFER, tx.getType());
                    org.junit.jupiter.api.Assertions.assertEquals(ACC_ORIGIN, tx.getAccountFrom());
                    org.junit.jupiter.api.Assertions.assertEquals(ACC_DEST, tx.getAccountTo());
                    org.junit.jupiter.api.Assertions.assertEquals(AMOUNT_200, tx.getAmount());
                })
                .verifyComplete();

        verify(accountClient).withdraw(10L, AMOUNT_200);
        verify(accountClient).deposit(99L, AMOUNT_200);
        verify(repository).save(any(Transaction.class));
    }

    @Test
    void givenInvalidAmount_whenTransfer_thenInvalidTransaction() {
        StepVerifier.create(service.transfer("A", "B", 0.0))
                .expectError(InvalidTransactionException.class)
                .verify();

        verifyNoInteractions(accountClient);
        verify(repository, never()).save(any());
    }

    // ====== HISTORIAL ======

    @Test
    void givenFilters_whenHistory_thenFiltersByAccountTypeAndDates() {
        // Arrange
        Instant now = Instant.now();
        Instant yesterday = now.minusSeconds(86400);
        Instant twoDaysAgo = now.minusSeconds(86400 * 2);

        Transaction t1 = Transaction.builder()
                .id("1").type(TransactionType.DEPOSIT).amount(100.0)
                .accountTo(ACC_1).date(twoDaysAgo).build();
        Transaction t2 = Transaction.builder()
                .id("2").type(TransactionType.WITHDRAW).amount(50.0)
                .accountFrom(ACC_1).date(yesterday).build();
        Transaction t3 = Transaction.builder()
                .id("3").type(TransactionType.TRANSFER).amount(10.0)
                .accountFrom("X").accountTo("Y").date(now).build();

        when(repository.findByAccountFromOrAccountToOrderByDateDesc(ACC_1, ACC_1))
                .thenReturn(Flux.just(t1, t2, t3));

        LocalDate desde = LocalDate.now().minusDays(2);
        LocalDate hasta = LocalDate.now();

        // Act
        Flux<Transaction> result = service.history(ACC_1, "WITHDRAW", desde, hasta);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(tx -> tx.getId().equals("2") && tx.getType() == TransactionType.WITHDRAW)
                .verifyComplete();

        verify(repository).findByAccountFromOrAccountToOrderByDateDesc(ACC_1, ACC_1);
    }

    // --- DEPOSITO: cuenta no existe (switchIfEmpty) ---
    @Test
    void givenUnknownAccount_whenDeposit_thenAccountNotFound() {
        when(accountClient.getByAccountNumber("NA")).thenReturn(Mono.empty());
        StepVerifier.create(service.deposit("NA", 10.0))
                .expectErrorMatches(e -> e.getClass().getSimpleName().equals("AccountNotFoundException"))
                .verify();
        verify(repository, never()).save(any());
    }

    @Test
    void givenUnknownAccount_whenWithdraw_thenAccountNotFound() {
        when(accountClient.getByAccountNumber("NA")).thenReturn(Mono.empty());
        StepVerifier.create(service.withdraw("NA", 10.0))
                .expectErrorMatches(e -> e.getClass().getSimpleName().equals("AccountNotFoundException"))
                .verify();
        verify(repository, never()).save(any());
    }

    @Test
    void givenSameOriginAndDestination_whenTransfer_thenInvalidTransaction() {
        StepVerifier.create(service.transfer("ACC-1","ACC-1", 50.0))
                .expectErrorMatches(e -> e.getClass().getSimpleName().equals("InvalidTransactionException"))
                .verify();
        verifyNoInteractions(accountClient);
        verify(repository, never()).save(any());
    }

    @Test
    void history_withoutFilters_returnsAll() {
        var t = Transaction.builder().id("x").type(TransactionType.DEPOSIT).amount(1.0)
                .date(Instant.now()).accountTo("A").build();
        when(repository.findAll()).thenReturn(Flux.just(t));

        StepVerifier.create(service.history(null, null, null, null))
                .expectNextCount(1)
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void history_dateBoundaries_inclusiveEndOfDay() {
        String acc = "ACC-9";
        LocalDate desde = LocalDate.now().minusDays(1);
        LocalDate hasta = LocalDate.now(); // inclusivo hasta fin de día

        Instant endOfHastaExclusive = hasta.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        Transaction in = Transaction.builder()
                .id("in").type(TransactionType.DEPOSIT)
                .date(endOfHastaExclusive.minusSeconds(1))
                .accountTo(acc).amount(10.0).build();

        Transaction after = Transaction.builder()
                .id("after").type(TransactionType.DEPOSIT)
                .date(endOfHastaExclusive.plusSeconds(1))
                .accountTo(acc).amount(10.0).build();

        when(repository.findByAccountFromOrAccountToOrderByDateDesc(acc, acc))
                .thenReturn(Flux.just(in, after));

        StepVerifier.create(service.history(acc, null, desde, hasta))
                .expectNextMatches(tx -> tx.getId().equals("in"))
                .verifyComplete();

        verify(repository).findByAccountFromOrAccountToOrderByDateDesc(acc, acc);
        verifyNoMoreInteractions(repository);
    }

    // ===== TRANSFER: origen NO encontrado =====
    @Test
    void givenUnknownOrigin_whenTransfer_thenAccountNotFound_andNoSideEffects() {
        when(accountClient.getByAccountNumber(ACC_ORIGIN)).thenReturn(Mono.empty());

        StepVerifier.create(service.transfer(ACC_ORIGIN, ACC_DEST, AMOUNT_200))
                .expectErrorMatches(e -> e.getClass().getSimpleName().equals("AccountNotFoundException"))
                .verify();

        verify(accountClient).getByAccountNumber(ACC_ORIGIN);
        verify(accountClient, never()).getByAccountNumber(ACC_DEST);
        verify(accountClient, never()).withdraw(anyLong(), anyDouble());
        verify(accountClient, never()).deposit(anyLong(), anyDouble());
        verify(repository, never()).save(any());
    }

    // ===== TRANSFER: destino NO encontrado =====
    @Test
    void givenUnknownDestination_whenTransfer_thenAccountNotFound_andNoWithdrawOrDeposit() {
        when(accountClient.getByAccountNumber(ACC_ORIGIN))
                .thenReturn(Mono.just(accDto(10L, ACC_ORIGIN, BAL_1000)));
        when(accountClient.getByAccountNumber(ACC_DEST)).thenReturn(Mono.empty());

        StepVerifier.create(service.transfer(ACC_ORIGIN, ACC_DEST, AMOUNT_200))
                .expectErrorMatches(e -> e.getClass().getSimpleName().equals("AccountNotFoundException"))
                .verify();

        verify(accountClient).getByAccountNumber(ACC_ORIGIN);
        verify(accountClient).getByAccountNumber(ACC_DEST);
        verify(accountClient, never()).withdraw(anyLong(), anyDouble());
        verify(accountClient, never()).deposit(anyLong(), anyDouble());
        verify(repository, never()).save(any());
    }

    // ===== HISTORY: solo 'desde' (inclusivo) =====
    @Test
    void givenOnlyFromDate_whenHistory_thenIncludesFromStartOfDayAndAfter() {
        String acc = ACC_1;
        LocalDate desde = LocalDate.now().minusDays(1);

        Instant edgeFrom = desde.atStartOfDay().toInstant(ZoneOffset.UTC); // incluye
        Instant before   = edgeFrom.minusSeconds(1);                       // excluye
        Instant after    = edgeFrom.plusSeconds(10);                       // incluye

        Transaction tBefore = Transaction.builder().id("B").type(TransactionType.DEPOSIT)
                .accountTo(acc).amount(1.0).date(before).build();
        Transaction tEdge   = Transaction.builder().id("E").type(TransactionType.DEPOSIT)
                .accountTo(acc).amount(1.0).date(edgeFrom).build();
        Transaction tAfter  = Transaction.builder().id("A").type(TransactionType.WITHDRAW)
                .accountFrom(acc).amount(1.0).date(after).build();

        when(repository.findByAccountFromOrAccountToOrderByDateDesc(acc, acc))
                .thenReturn(Flux.just(tBefore, tEdge, tAfter));

        StepVerifier.create(service.history(acc, null, desde, null))
                .expectNextMatches(tx -> tx.getId().equals("E"))
                .expectNextMatches(tx -> tx.getId().equals("A"))
                .verifyComplete();

        verify(repository).findByAccountFromOrAccountToOrderByDateDesc(acc, acc);
    }

    // ===== HISTORY: solo 'hasta' (inclusivo hasta fin de día) =====
    @Test
    void givenOnlyToDate_whenHistory_thenIncludesUntilEndOfDay() {
        String acc = ACC_1;
        LocalDate hasta = LocalDate.now();

        Instant endExclusive = hasta.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endInclusive = endExclusive.minusSeconds(1); // incluye
        Instant after        = endExclusive.plusSeconds(1);  // excluye

        Transaction tEnd   = Transaction.builder().id("END").type(TransactionType.DEPOSIT)
                .accountTo(acc).amount(1.0).date(endInclusive).build();
        Transaction tAfter = Transaction.builder().id("AFTER").type(TransactionType.DEPOSIT)
                .accountTo(acc).amount(1.0).date(after).build();

        when(repository.findByAccountFromOrAccountToOrderByDateDesc(acc, acc))
                .thenReturn(Flux.just(tEnd, tAfter));

        StepVerifier.create(service.history(acc, null, null, hasta))
                .expectNextMatches(tx -> tx.getId().equals("END"))
                .verifyComplete();

        verify(repository).findByAccountFromOrAccountToOrderByDateDesc(acc, acc);
    }

    @Test
    void givenDepositFails_whenTransfer_thenPropagateError_andDoNotSave() {
        when(accountClient.getByAccountNumber(ACC_ORIGIN))
                .thenReturn(Mono.just(accDto(10L, ACC_ORIGIN, BAL_1000)));
        when(accountClient.getByAccountNumber(ACC_DEST))
                .thenReturn(Mono.just(accDto(99L, ACC_DEST, BAL_1000)));

        when(accountClient.withdraw(10L, AMOUNT_200))
                .thenReturn(Mono.just(accDto(10L, ACC_ORIGIN, BAL_1000 - AMOUNT_200)));
        when(accountClient.deposit(99L, AMOUNT_200))
                .thenReturn(Mono.error(new RuntimeException("deposit failed")));

        StepVerifier.create(service.transfer(ACC_ORIGIN, ACC_DEST, AMOUNT_200))
                .expectError(RuntimeException.class)
                .verify();

        verify(repository, never()).save(any());
    }

}
