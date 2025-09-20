package com.bank.accountms.api.dto;

import com.bank.accountms.api.dto.AccountDtos.AccountCreateDto;
import com.bank.accountms.api.dto.AccountDtos.AccountDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountDtosTest {

  @Test
  void createAccountDtoRecord() {
    AccountCreateDto dto = new AccountCreateDto(10L, "SAVINGS", 500.0);

    assertEquals(10L, dto.customerId());
    assertEquals("SAVINGS", dto.accountType());
    assertEquals(500.0, dto.initialDeposit());
  }

  @Test
  void accountDtoRecord() {
    AccountDto dto = new AccountDto(1L, "456", 300.0, "CHECKING", 20L);

    assertEquals(1L, dto.id());
    assertEquals("456", dto.accountNumber());
    assertEquals(300.0, dto.balance());
    assertEquals("CHECKING", dto.accountType());
    assertEquals(20L, dto.customerId());
  }
}

