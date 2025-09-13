package com.bank.accountms.service;

import com.bank.accountms.domain.Account;
import com.bank.accountms.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGet_ReturnsAccount() {
        Account account = new Account();
        account.setId(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        Account result = accountService.get(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGet_ThrowsWhenNotFound() {
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> accountService.get(2L));
    }

    @Test
    void testCreate_Success() {
        var dto = mock(com.bank.accountms.api.dto.AccountDtos.AccountCreateDto.class);
        when(dto.accountType()).thenReturn("SAVINGS");
        when(dto.initialDeposit()).thenReturn(1000.0);
        when(dto.customerId()).thenReturn(123L);

        Account saved = new Account();
        when(accountRepository.save(any(Account.class))).thenReturn(saved);

        Account result = accountService.create(dto);
        assertNotNull(result);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void testDeposit_Success() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(100.0);
        account.setAccountType(Account.AccountType.SAVINGS);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        var dto = mock(com.bank.accountms.api.dto.AccountDtos.AmountDto.class);
        when(dto.amount()).thenReturn(50.0);

        Account result = accountService.deposit(1L, dto);
        assertEquals(150.0, result.getBalance());
    }

    @Test
    void testWithdraw_Success() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(200.0);
        account.setAccountType(Account.AccountType.SAVINGS);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        var dto = mock(com.bank.accountms.api.dto.AccountDtos.AmountDto.class);
        when(dto.amount()).thenReturn(50.0);

        Account result = accountService.withdraw(1L, dto);
        assertEquals(150.0, result.getBalance());
    }

    @Test
    void testDelete_CallsRepository() {
        doNothing().when(accountRepository).deleteById(1L);
        accountService.delete(1L);
        verify(accountRepository).deleteById(1L);
    }

    @Test
    void testList_ReturnsAllAccounts_WhenCustomerIdIsNull() {
        Account acc1 = new Account();
        Account acc2 = new Account();
        when(accountRepository.findAll()).thenReturn(List.of(acc1, acc2));

        List<Account> result = accountService.list(null);

        assertEquals(2, result.size());
        verify(accountRepository).findAll();
        verify(accountRepository, never()).findByCustomerId(anyLong());
    }

    @Test
    void testList_ReturnsAccountsForCustomer_WhenCustomerIdIsProvided() {
        Account acc1 = new Account();
        Account acc2 = new Account();
        when(accountRepository.findByCustomerId(123L)).thenReturn(List.of(acc1, acc2));

        List<Account> result = accountService.list(123L);

        assertEquals(2, result.size());
        verify(accountRepository).findByCustomerId(123L);
        verify(accountRepository, never()).findAll();
    }
    @Test
    void testTotalBalanceAsync_ReturnsSumOfBalances() throws Exception {
        Account acc1 = new Account();
        acc1.setBalance(100.0);
        Account acc2 = new Account();
        acc2.setBalance(200.0);
        when(accountRepository.findByCustomerId(123L)).thenReturn(List.of(acc1, acc2));

        CompletableFuture<Double> future = accountService.totalBalanceAsync(123L);
        Double result = future.get(); // Wait for completion

        assertEquals(300.0, result);
        verify(accountRepository).findByCustomerId(123L);
    }

    @Test
    void testTotalBalanceAsync_ReturnsZeroWhenNoAccounts() throws Exception {
        when(accountRepository.findByCustomerId(999L)).thenReturn(List.of());

        CompletableFuture<Double> future = accountService.totalBalanceAsync(999L);
        Double result = future.get();

        assertEquals(0.0, result);
        verify(accountRepository).findByCustomerId(999L);
    }
    
}
