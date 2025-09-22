package com.transactionms.service.impl;

import com.transactionms.factory.TransactionFactory;
import com.transactionms.repository.TransactionRepository;
import com.transactionms.repository.model.Transaction;
import com.transactionms.repository.model.TransactionType;
import com.transactionms.service.AccountService;
import com.transactionms.validator.TransactionValidator;
import com.transactionms.client.dto.AccountDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.Mockito.*;

/**
 * Test example mostrando las mejoras de testabilidad después de la refactorización SOLID.
 * 
 * BENEFICIOS DE LA REFACTORIZACIÓN:
 * ✅ SRP: Cada dependency tiene una responsabilidad específica y es fácil de mockear
 * ✅ DIP: AccountService es una abstracción, fácil de testear
 * ✅ Testabilidad: TransactionFactory con Clock permite controlar tiempo en tests
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceImplRefactoredTest {

    @Mock private TransactionRepository repository;
    @Mock private AccountService accountService; // ✅ DIP: Mock de abstracción
    @Mock private TransactionValidator validator; // ✅ SRP: Mock de validador separado
    @Mock private TransactionFactory transactionFactory; // ✅ SRP: Mock de factory separado
    
    @InjectMocks private TransactionServiceImpl service;

    private static final String ACCOUNT_NUMBER = "ACC-123";
    private static final Double AMOUNT = 100.0;
    private static final Instant FIXED_TIME = Instant.parse("2025-09-21T12:00:00Z");

    @BeforeEach
    void setup() {
        reset(repository, accountService, validator, transactionFactory);
    }

    @Test
    void deposit_withRefactoredCode_shouldUseNewAbstractions() {
        // Arrange
        AccountDto account = createAccountDto();
        Transaction expectedTransaction = createDepositTransaction();
        
        // ✅ SRP: Validación es responsabilidad separada - no necesitamos testearla aquí
        doNothing().when(validator).validateDeposit(ACCOUNT_NUMBER, AMOUNT);
        
        when(accountService.getByAccountNumber(ACCOUNT_NUMBER))
                .thenReturn(Mono.just(account));
        when(accountService.depositByAccountNumber(ACCOUNT_NUMBER, AMOUNT))
                .thenReturn(Mono.just(account));
        
        // ✅ SRP: Factory es responsabilidad separada - controlamos qué retorna
        when(transactionFactory.createDeposit(ACCOUNT_NUMBER, AMOUNT))
                .thenReturn(expectedTransaction);
        when(repository.save(expectedTransaction))
                .thenReturn(Mono.just(expectedTransaction));

        // Act & Assert
        StepVerifier.create(service.deposit(ACCOUNT_NUMBER, AMOUNT))
                .expectNext(expectedTransaction)
                .verifyComplete();

        // Verify interactions
        verify(validator).validateDeposit(ACCOUNT_NUMBER, AMOUNT); // ✅ Validación delegada
        verify(accountService).getByAccountNumber(ACCOUNT_NUMBER); // ✅ DIP: usando abstracción
        verify(transactionFactory).createDeposit(ACCOUNT_NUMBER, AMOUNT); // ✅ Construcción delegada
        verify(repository).save(expectedTransaction);
    }

    @Test
    void deposit_whenValidationFails_shouldNotCallOtherServices() {
        // Arrange
        doThrow(new RuntimeException("Invalid amount"))
                .when(validator).validateDeposit(ACCOUNT_NUMBER, AMOUNT);

        // Act & Assert
        StepVerifier.create(service.deposit(ACCOUNT_NUMBER, AMOUNT))
                .expectError(RuntimeException.class)
                .verify();

        // ✅ SRP: Si la validación falla, no se llaman otros servicios
        verifyNoInteractions(accountService, transactionFactory, repository);
    }

    private AccountDto createAccountDto() {
        AccountDto account = new AccountDto();
        account.setId(1L);
        account.setAccountNumber(ACCOUNT_NUMBER);
        account.setBalance(500.0);
        return account;
    }

    private Transaction createDepositTransaction() {
        return Transaction.builder()
                .id("tx-123")
                .type(TransactionType.DEPOSIT)
                .accountTo(ACCOUNT_NUMBER)
                .amount(AMOUNT)
                .date(FIXED_TIME)
                .build();
    }
}