package com.bank.accountms.mapper;

import com.bank.accountms.api.dto.AccountDtos.AccountDto;
import com.bank.accountms.api.mapper.AccountMapper;
import com.bank.accountms.domain.Account;
import com.bank.accountms.domain.Account.AccountType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccountMapperTest {

  @Test
  void toDtoShouldMapCorrectly() {
    Account account = new Account();
    account.setId(1L);
    account.setAccountNumber("12345");
    account.setBalance(200.0);
    account.setAccountType(AccountType.SAVINGS);
    account.setCustomerId(10L);

    AccountDto dto = AccountMapper.toDto(account);

    assertEquals(account.getId(), dto.id());
    assertEquals(account.getAccountNumber(), dto.accountNumber());
    assertEquals(account.getBalance(), dto.balance());
    assertEquals(account.getAccountType().name(), dto.accountType());
    assertEquals(account.getCustomerId(), dto.customerId());

  }
}
