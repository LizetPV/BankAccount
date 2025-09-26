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
  void testList_WithPageable_WhenCustomerIdIsNull() {
    var pageable = mock(org.springframework.data.domain.Pageable.class);
    Account acc1 = new Account();
    Account acc2 = new Account();

    when(accountRepository.findAll(pageable))
        .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(acc1, acc2)));

    var result = accountService.list(null, pageable);

    assertEquals(2, result.getContent().size());
    verify(accountRepository).findAll(pageable);
    verify(accountRepository, never()).findByCustomerId(anyLong(), any());
  }

  @Test
  void testWithdraw_ThrowsWhenAmountNonPositive() {
    var dto = mock(com.bank.accountms.api.dto.AccountDtos.AmountDto.class);
    when(dto.amount()).thenReturn(0.0);

    assertThrows(IllegalArgumentException.class, () -> accountService.withdraw(1L, dto));
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

  @Test
  void testDepositByNumber_Success() {
    Account account = new Account();
    account.setAccountNumber("ACC123");
    account.setBalance(100.0);
    account.setAccountType(Account.AccountType.SAVINGS);

    when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));
    when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

    var dto = mock(com.bank.accountms.api.dto.AccountDtos.AmountDto.class);
    when(dto.amount()).thenReturn(50.0);

    Account result = accountService.depositByNumber("ACC123", dto);
    assertEquals(150.0, result.getBalance());
    verify(accountRepository).findByAccountNumber("ACC123");
  }

  @Test
  void testWithdrawByNumber_Success() {
    Account account = new Account();
    account.setAccountNumber("ACC123");
    account.setBalance(200.0);
    account.setAccountType(Account.AccountType.SAVINGS);

    when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));
    when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

    var dto = mock(com.bank.accountms.api.dto.AccountDtos.AmountDto.class);
    when(dto.amount()).thenReturn(50.0);

    Account result = accountService.withdrawByNumber("ACC123", dto);
    assertEquals(150.0, result.getBalance());
    verify(accountRepository).findByAccountNumber("ACC123");
  }

  @Test
  void testDepositByNumber_ThrowsWhenAmountNonPositive() {
    var dto = mock(com.bank.accountms.api.dto.AccountDtos.AmountDto.class);
    when(dto.amount()).thenReturn(0.0);

    assertThrows(IllegalArgumentException.class, () -> accountService.depositByNumber("ACC123", dto));
  }

  @Test
  void testWithdrawByNumber_ThrowsWhenAmountNonPositive() {
    var dto = mock(com.bank.accountms.api.dto.AccountDtos.AmountDto.class);
    when(dto.amount()).thenReturn(-10.0);

    assertThrows(IllegalArgumentException.class, () -> accountService.withdrawByNumber("ACC123", dto));
  }

  @Test
  void testWithdrawByNumber_ThrowsWhenSavingsInsufficient() {
    Account account = new Account();
    account.setAccountNumber("ACC123");
    account.setBalance(50.0);
    account.setAccountType(Account.AccountType.SAVINGS);

    when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));

    var dto = mock(com.bank.accountms.api.dto.AccountDtos.AmountDto.class);
    when(dto.amount()).thenReturn(100.0);

    assertThrows(IllegalStateException.class, () -> accountService.withdrawByNumber("ACC123", dto));
  }

  @Test
  void testWithdrawByNumber_ThrowsWhenCheckingExceedsOverdraftLimit() {
    Account account = new Account();
    account.setAccountNumber("ACC123");
    account.setBalance(0.0);
    account.setAccountType(Account.AccountType.CHECKING);

    when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));

    var dto = mock(com.bank.accountms.api.dto.AccountDtos.AmountDto.class);
    when(dto.amount()).thenReturn(600.0); // deja saldo en -600

    assertThrows(IllegalStateException.class, () -> accountService.withdrawByNumber("ACC123", dto));
  }

  @Test
  void testWithdrawByNumber_AllowsCheckingOverdraftUpToMinus500() {
    Account account = new Account();
    account.setAccountNumber("ACC123");
    account.setBalance(100.0);
    account.setAccountType(Account.AccountType.CHECKING);

    when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));
    when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

    var dto = mock(com.bank.accountms.api.dto.AccountDtos.AmountDto.class);
    when(dto.amount()).thenReturn(600.0); // saldo final: -500

    Account result = accountService.withdrawByNumber("ACC123", dto);
    assertEquals(-500.0, result.getBalance());
  }

  @Test
  void testWithdrawByNumber_SavingsWithdrawExactBalance() {
    Account account = new Account();
    account.setAccountNumber("ACC123");
    account.setBalance(100.0);
    account.setAccountType(Account.AccountType.SAVINGS);

    when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));
    when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

    var dto = mock(com.bank.accountms.api.dto.AccountDtos.AmountDto.class);
    when(dto.amount()).thenReturn(100.0); // saldo quedará en 0

    Account result = accountService.withdrawByNumber("ACC123", dto);
    assertEquals(0.0, result.getBalance());
  }

  @Test
  void testGetByAccountNumber_Success() {
    Account account = new Account();
    account.setAccountNumber("ACC123");
    when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));

    Account result = accountService.getByAccountNumber("ACC123");
    assertNotNull(result);
    assertEquals("ACC123", result.getAccountNumber());
  }

  @Test
  void testList_WithPageable_Success() {
    Account acc1 = new Account();
    Account acc2 = new Account();
    var pageable = mock(org.springframework.data.domain.Pageable.class);

    when(accountRepository.findByCustomerId(123L, pageable))
        .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(acc1, acc2)));

    var result = accountService.list(123L, pageable);

    assertEquals(2, result.getContent().size());
    verify(accountRepository).findByCustomerId(123L, pageable);
  }


  @Test
  void testGetByAccountNumber_NotFound() {
    when(accountRepository.findByAccountNumber("ACC999")).thenReturn(Optional.empty());
    assertThrows(NoSuchElementException.class, () -> accountService.getByAccountNumber("ACC999"));
  }


  @Test
    void testCreate_ThrowsWhenInitialDepositIsZero() {
      var dto = mock(com.bank.accountms.api.dto.AccountDtos.AccountCreateDto.class);
      when(dto.accountType()).thenReturn("SAVINGS");
      when(dto.initialDeposit()).thenReturn(0.0);
      when(dto.customerId()).thenReturn(123L);

      assertThrows(IllegalArgumentException.class, () -> accountService.create(dto));
    }

    @Test
    void testWithdraw_ThrowsWhenSavingsInsufficient() {
      Account account = new Account();
      account.setId(1L);
      account.setBalance(50.0);
      account.setAccountType(Account.AccountType.SAVINGS);

      when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

      var dto = mock(com.bank.accountms.api.dto.AccountDtos.AmountDto.class);
      when(dto.amount()).thenReturn(100.0);

      assertThrows(IllegalStateException.class, () -> accountService.withdraw(1L, dto));
    }

  @Test
  void testWithdraw_ThrowsWhenCheckingExceedsOverdraftLimit() {
    Account account = new Account();
    account.setId(1L);
    account.setBalance(0.0);
    account.setAccountType(Account.AccountType.CHECKING);

    when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

    var dto = mock(com.bank.accountms.api.dto.AccountDtos.AmountDto.class);
    when(dto.amount()).thenReturn(600.0); // Esto dejaría saldo en -600

    assertThrows(IllegalStateException.class, () -> accountService.withdraw(1L, dto));
  }

  @Test
  void testWithdraw_AllowsCheckingOverdraftUpToMinus500() {
    Account account = new Account();
    account.setId(1L);
    account.setBalance(100.0);
    account.setAccountType(Account.AccountType.CHECKING);

    when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
    when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

    var dto = mock(com.bank.accountms.api.dto.AccountDtos.AmountDto.class);
    when(dto.amount()).thenReturn(600.0); // Saldo final: -500

    Account result = accountService.withdraw(1L, dto);
    assertEquals(-500.0, result.getBalance());
  }

  @Test
  void testDeposit_ThrowsWhenAmountNonPositive() {
    var dto = mock(com.bank.accountms.api.dto.AccountDtos.AmountDto.class);
    when(dto.amount()).thenReturn(0.0);
    assertThrows(IllegalArgumentException.class, () -> accountService.deposit(1L, dto));
  }

  @Test
  void testGetAccount_NotFound_Throws() {
    when(accountRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(NoSuchElementException.class, () -> accountService.get(99L));
  }

}
