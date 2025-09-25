package com.bank.customerms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class CustomerMicroserviceApplicationTest {

  @Test
  void contextLoads() {
    assertDoesNotThrow(() -> CustomerMicroserviceApplication.main(new String[]{}));
  }
}
