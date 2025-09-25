package com.transactionms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación principal del microservicio de transacciones.
 */
@SpringBootApplication
public class TransactionMsApplication {

  /**
   * Método principal que arranca la aplicación Spring Boot.
   *
   * @param args argumentos de línea de comandos
   */
  public static void main(String[] args) {
    SpringApplication.run(TransactionMsApplication.class, args);
  }
}
