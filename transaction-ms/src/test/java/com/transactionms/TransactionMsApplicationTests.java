package com.transactionms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TransactionMsApplicationTests {

  @Test
  void main_runsWithoutExceptions() {
    // Verificamos que el main arranca sin lanzar excepciones
    assertDoesNotThrow(() -> TransactionMsApplication.main(new String[]{}));
  }
}

