package com.bank.accountms.api;

import com.bank.accountms.contract.model.*;
import com.bank.accountms.domain.Account;
import com.bank.accountms.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private AutoCloseable closeable;
    @BeforeEach
    void setUp() {
        //  2) Guardamos la referencia a los mocks abiertos
        closeable = MockitoAnnotations.openMocks(this);
    }
    //  3) Cerramos los mocks después de cada test
    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) closeable.close();
    }
    @Test
    void testListAccounts() {
        Account acc = new Account();
        acc.setId(1L);
        acc.setAccountNumber("ACC1");
        acc.setBalance(100.0);
        acc.setAccountType(Account.AccountType.SAVINGS);
        acc.setCustomerId(10L);
        List<Account> accounts = List.of(acc);
        Page<Account> page = new PageImpl<>(accounts, PageRequest.of(0, 10), 1);
        when(accountService.list(anyLong(), any(Pageable.class))).thenReturn(page);

        ResponseEntity<AccountPage> response = accountController.listAccounts(10L, 0, 10, "id,desc");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void testCreateAccount() {
        AccountCreateDto dto = new AccountCreateDto();
        dto.setCustomerId(10L);
        dto.setAccountType(AccountCreateDto.AccountTypeEnum.SAVINGS);
        dto.setInitialDeposit(100.0);
        Account acc = new Account();
        acc.setId(1L);
        acc.setAccountNumber("ACC1");
        acc.setBalance(100.0);
        acc.setAccountType(Account.AccountType.SAVINGS);
        acc.setCustomerId(10L);
        when(accountService.create(any())).thenReturn(acc);

        ResponseEntity<AccountDto> response = accountController.createAccount(dto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ACC1", response.getBody().getAccountNumber());
    }

    @Test
    void testGetAccount() {
        Account acc = new Account();
        acc.setId(1L);
        acc.setAccountNumber("ACC1");
        acc.setBalance(100.0);
        acc.setAccountType(Account.AccountType.SAVINGS);
        acc.setCustomerId(10L);
        when(accountService.get(1L)).thenReturn(acc);

        ResponseEntity<AccountDto> response = accountController.getAccount(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testDeleteAccount() {
        doNothing().when(accountService).delete(1L);
        ResponseEntity<Void> response = accountController.deleteAccount(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeposit() {
        AmountDto dto = new AmountDto();
        dto.setAmount(50.0);
        Account acc = new Account();
        acc.setId(1L);
        acc.setAccountNumber("ACC1");
        acc.setBalance(150.0);
        acc.setAccountType(Account.AccountType.SAVINGS);
        acc.setCustomerId(10L);
        when(accountService.deposit(eq(1L), any())).thenReturn(acc);

        ResponseEntity<AccountDto> response = accountController.deposit(1L, dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(150.0, response.getBody().getBalance());
    }

    @Test
    void testWithdraw() {
        AmountDto dto = new AmountDto();
        dto.setAmount(30.0);
        Account acc = new Account();
        acc.setId(1L);
        acc.setAccountNumber("ACC1");
        acc.setBalance(70.0);
        acc.setAccountType(Account.AccountType.SAVINGS);
        acc.setCustomerId(10L);
        when(accountService.withdraw(eq(1L), any())).thenReturn(acc);

        ResponseEntity<AccountDto> response = accountController.withdraw(1L, dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(70.0, response.getBody().getBalance());
    }

    @Test
    void testTotalBalance() {
        when(accountService.totalBalanceAsync(10L)).thenReturn(CompletableFuture.completedFuture(1000.0));
        ResponseEntity<Double> response = accountController.totalBalance(10L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1000.0, response.getBody());
    }

    @Test
    void testGetAccountByNumber() {
        Account acc = new Account();
        acc.setId(1L);
        acc.setAccountNumber("ACC1");
        acc.setBalance(100.0);
        acc.setAccountType(Account.AccountType.SAVINGS);
        acc.setCustomerId(10L);
        when(accountService.getByAccountNumber("ACC1")).thenReturn(acc);

        ResponseEntity<AccountDto> response = accountController.getAccountByNumber("ACC1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ACC1", response.getBody().getAccountNumber());
    }

    @Test
    void testDepositByAccountNumber() {
        AmountDto dto = new AmountDto();
        dto.setAmount(100.0);
        Account acc = new Account();
        acc.setId(1L);
        acc.setAccountNumber("ACC1");
        acc.setBalance(200.0);
        acc.setAccountType(Account.AccountType.SAVINGS);
        acc.setCustomerId(10L);
        when(accountService.depositByNumber(eq("ACC1"), any())).thenReturn(acc);

        ResponseEntity<AccountDto> response = accountController.depositByAccountNumber("ACC1", dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(200.0, response.getBody().getBalance());
    }

    @Test
    void testWithdrawByAccountNumber() {
        AmountDto dto = new AmountDto();
        dto.setAmount(50.0);
        Account acc = new Account();
        acc.setId(1L);
        acc.setAccountNumber("ACC1");
        acc.setBalance(150.0);
        acc.setAccountType(Account.AccountType.SAVINGS);
        acc.setCustomerId(10L);
        when(accountService.withdrawByNumber(eq("ACC1"), any())).thenReturn(acc);

        ResponseEntity<AccountDto> response = accountController.withdrawByAccountNumber("ACC1", dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(150.0, response.getBody().getBalance());
    }
  // ---- Tests adicionales para parseSort y listAccounts ----

  @Test
  void testListAccounts_sortNull_defaultsToIdDesc() {
    Page<Account> page = new PageImpl<>(List.of());
    when(accountService.list(anyLong(), any(Pageable.class))).thenReturn(page);

    ResponseEntity<AccountPage> response = accountController.listAccounts(1L, 0, 10, null);

    assertTrue(response.getBody().getSort().contains("id: DESC"));
  }

  @Test
  void testListAccounts_sortBlank_defaultsToIdDesc() {
    Page<Account> page = new PageImpl<>(List.of());
    when(accountService.list(anyLong(), any(Pageable.class))).thenReturn(page);

    ResponseEntity<AccountPage> response = accountController.listAccounts(1L, 0, 10, "   ");

    assertTrue(response.getBody().getSort().contains("id: DESC"));
  }

  @Test
  void testListAccounts_sortAsc() {
    Page<Account> page = new PageImpl<>(List.of());
    when(accountService.list(anyLong(), any(Pageable.class))).thenReturn(page);

    ResponseEntity<AccountPage> response = accountController.listAccounts(1L, 0, 10, "accountNumber,asc");

    assertTrue(response.getBody().getSort().contains("accountNumber: ASC"));
  }

  @Test
  void testListAccounts_sortDesc() {
    Page<Account> page = new PageImpl<>(List.of());
    when(accountService.list(anyLong(), any(Pageable.class))).thenReturn(page);

    ResponseEntity<AccountPage> response = accountController.listAccounts(1L, 0, 10, "balance,desc");

    assertTrue(response.getBody().getSort().contains("balance: DESC"));
  }

  @Test
  void testListAccounts_sortInvalid_fallbackToDefault() {
    Page<Account> page = new PageImpl<>(List.of());
    when(accountService.list(anyLong(), any(Pageable.class))).thenReturn(page);

    // ",," forzará un split raro -> cae en catch -> default sort
    ResponseEntity<AccountPage> response = accountController.listAccounts(1L, 0, 10, ",,");

    assertTrue(response.getBody().getSort().contains("id: DESC"));
  }

  @Test
  void testListAccounts_pageNull_defaultsToZero() {
    Page<Account> page = new PageImpl<>(List.of());
    when(accountService.list(any(), any(Pageable.class))).thenReturn(page);

    ResponseEntity<AccountPage> response = accountController.listAccounts(1L, null, 10, "id,desc");
    assertEquals(0, response.getBody().getNumber());
  }

  @Test
  void testListAccounts_pageNegative_defaultsToZero() {
    Page<Account> page = new PageImpl<>(List.of());
    when(accountService.list(any(), any(Pageable.class))).thenReturn(page);

    ResponseEntity<AccountPage> response = accountController.listAccounts(1L, -5, 10, "id,desc");
    assertEquals(0, response.getBody().getNumber());
  }


  @Test
  void testParseSort_withoutDirection_defaultsToAsc() {
    Page<Account> page = new PageImpl<>(List.of());
    when(accountService.list(any(), any(Pageable.class))).thenReturn(page);

    ResponseEntity<AccountPage> response = accountController.listAccounts(1L, 0, 10, "accountNumber");
    assertTrue(response.getBody().getSort().contains("accountNumber: ASC"));
  }

}
