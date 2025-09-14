package com.bank.accountms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de arranque del microservicio de cuentas.
 */
@SpringBootApplication
public class AccountMicroserviceApplication {

    /**
     * Método principal para ejecutar la aplicación.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(AccountMicroserviceApplication.class, args);
    }
}
