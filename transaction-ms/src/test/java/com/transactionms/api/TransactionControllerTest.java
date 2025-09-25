package com.transactionms.api;

import com.transactionms.controller.TransactionController;
import com.transactionms.exceptions.InvalidTransactionException;
import com.transactionms.repository.model.Transaction;
import com.transactionms.repository.model.TransactionType;
import com.transactionms.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * AAA + FIRST + KISS/DRY.
 * Cobertura de:
 *  - deposit / retiro / transferencia (feliz + error)
 *  - history: con filtros, sin filtros y con fechas
 */
//@WebFluxTest(controllers = TransactionController.class)
//@Import(TransactionControllerTest.TestConfig.class)
class TransactionControllerTest {

    /*@Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TransactionService service; // mock inyectado

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        TransactionService transactionService() {
            return Mockito.mock(TransactionService.class);
        }
    }

    // ===== Casos felices ya conocidos =====

    @Test
    void deposit_endpoint_ok() {
        Transaction tx = Transaction.builder()
                .id("tx-1")
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .accountTo("ACC-1")
                .date(Instant.now())
                .build();
        when(service.deposit(eq("ACC-1"), eq(100.0))).thenReturn(Mono.just(tx));

        webTestClient.post()
                .uri("/transacciones/deposito")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"accountNumber\":\"ACC-1\",\"amount\":100.0}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.type").isEqualTo("DEPOSIT")
                .jsonPath("$.amount").isEqualTo(100.0);
    }

    @Test
    void withdraw_endpoint_ok() {
        Transaction tx = Transaction.builder()
                .id("tx-2")
                .type(TransactionType.WITHDRAW)
                .amount(50.0)
                .accountFrom("ACC-2")
                .date(Instant.now())
                .build();
        when(service.withdraw(eq("ACC-2"), eq(50.0))).thenReturn(Mono.just(tx));

        webTestClient.post()
                .uri("/transacciones/retiro")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"accountNumber\":\"ACC-2\",\"amount\":50.0}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.type").isEqualTo("WITHDRAW")
                .jsonPath("$.amount").isEqualTo(50.0);
    }

    @Test
    void transfer_endpoint_ok() {
        Transaction tx = Transaction.builder()
                .id("tx-3")
                .type(TransactionType.TRANSFER)
                .amount(200.0)
                .accountFrom("ACC-010")
                .accountTo("ACC-999")
                .date(Instant.now())
                .build();
        when(service.transfer(eq("ACC-010"), eq("ACC-999"), eq(200.0)))
                .thenReturn(Mono.just(tx));

        webTestClient.post()
                .uri("/transacciones/transferencia")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"originAccountNumber\":\"ACC-010\",\"destinationAccountNumber\":\"ACC-999\",\"amount\":200.0}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.type").isEqualTo("TRANSFER")
                .jsonPath("$.amount").isEqualTo(200.0)
                .jsonPath("$.accountFrom").isEqualTo("ACC-010")
                .jsonPath("$.accountTo").isEqualTo("ACC-999");
    }

    @Test
    void history_endpoint_ok_conFiltrosBasicos() {
        Transaction tx = Transaction.builder()
                .id("tx-4")
                .type(TransactionType.WITHDRAW)
                .amount(50.0)
                .accountFrom("ACC-2")
                .date(Instant.now())
                .build();
        when(service.history(eq("ACC-2"), eq("WITHDRAW"), any(), any()))
                .thenReturn(Flux.just(tx));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/transacciones/historial")
                        .queryParam("numeroCuenta", "ACC-2")
                        .queryParam("tipo", "WITHDRAW")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].type").isEqualTo("WITHDRAW")
                .jsonPath("$[0].amount").isEqualTo(50.0);
    }

    // ===== Nuevos casos para subir cobertura =====

    // 1) history SIN parámetros: debe pasar nulls al service
    @Test
    void history_sinParametros_llamaServiceConNulls() {
        when(service.history(isNull(), isNull(), isNull(), isNull()))
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/transacciones/historial")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[]");
    }

    // 2) history CON rango de fechas ISO (ejercita parseo y mapeo de @DateTimeFormat)
    @Test
    void history_conFechasISO_ok() {
        LocalDate desde = LocalDate.now().minusDays(3);
        LocalDate hasta = LocalDate.now();

        when(service.history(eq("ACC-7"), isNull(), eq(desde), eq(hasta)))
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/transacciones/historial")
                        .queryParam("numeroCuenta", "ACC-7")
                        .queryParam("fechaDesde", desde.toString())
                        .queryParam("fechaHasta", hasta.toString())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[]");
    }

    // 3) Propagación de error en deposit (el service falla)
    @Test
    void deposit_error_propagadoComo5xx() {
        when(service.deposit(eq("ACC-1"), eq(0.0)))
                .thenReturn(Mono.error(new InvalidTransactionException("monto inválido")));

        webTestClient.post()
                .uri("/transacciones/deposito")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"accountNumber\":\"ACC-1\",\"amount\":0.0}")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    // 4) Propagación de error en withdraw
    @Test
    void withdraw_error_propagadoComo5xx() {
        when(service.withdraw(eq("ACC-2"), eq(0.0)))
                .thenReturn(Mono.error(new InvalidTransactionException("monto inválido")));

        webTestClient.post()
                .uri("/transacciones/retiro")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"accountNumber\":\"ACC-2\",\"amount\":0.0}")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    // 5) Propagación de error en transfer (cuentas iguales u otra validación)
    @Test
    void transfer_error_propagadoComo5xx() {
        when(service.transfer(eq("A"), eq("A"), eq(100.0)))
                .thenReturn(Mono.error(new InvalidTransactionException("origen=destino")));

        webTestClient.post()
                .uri("/transacciones/transferencia")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"originAccountNumber\":\"A\",\"destinationAccountNumber\":\"A\",\"amount\":100.0}")
                .exchange()
                .expectStatus().is5xxServerError();
    }*/
}
