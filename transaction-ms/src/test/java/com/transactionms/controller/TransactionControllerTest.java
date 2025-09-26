package com.transactionms.controller;

import com.transactionms.dto.DepositRequestDto;
import com.transactionms.dto.TransactionDto;
import com.transactionms.dto.TransferRequestDto;
import com.transactionms.dto.WithdrawalRequestDto;
import com.transactionms.repository.model.Transaction;
import com.transactionms.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de integración para TransactionController.
 * Verifica el mapeo de rutas, la deserialización de DTOs y la delegación al servicio mockeado.
 */
@WebFluxTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TransactionService service;

    // --- Constantes para pruebas ---
    private static final String ACC_1 = "ACC-001";
    private static final String ACC_2 = "ACC-002";
    private static final Double AMOUNT = 100.0;
    // Se usa un builder simple para el mock de la entidad
    private static final Transaction MOCKED_TX = Transaction.builder().id("TX-MOCK").build();
    // Se mapea la entidad mock al DTO que el controller debe retornar
    private static final TransactionDto MOCKED_DTO = TransactionDto.fromEntity(MOCKED_TX);

    // =================================================================
    // TESTS PARA DEPÓSITO
    // =================================================================

    @Test
    void whenPostDeposit_thenDelegatesToServiceAndReturns200() {
        // Arrange: Se usa el constructor vacío y setters para evitar error de compilación
        DepositRequestDto request = new DepositRequestDto();
        request.setAccountNumber(ACC_1);
        request.setAmount(AMOUNT);

        when(service.deposit(anyString(), anyDouble())).thenReturn(Mono.just(MOCKED_TX));

        // Act & Assert
        webTestClient.post().uri("/transacciones/deposito")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TransactionDto.class)
                .isEqualTo(MOCKED_DTO);

        // Verificación clave: Confirma que el controller llamó al servicio
        verify(service).deposit(ACC_1, AMOUNT);
    }

    // =================================================================
    // TESTS PARA RETIRO
    // =================================================================

    @Test
    void whenPostWithdraw_thenDelegatesToServiceAndReturns200() {
        // Arrange
        WithdrawalRequestDto request = new WithdrawalRequestDto();
        request.setAccountNumber(ACC_1);
        request.setAmount(AMOUNT);

        when(service.withdraw(anyString(), anyDouble())).thenReturn(Mono.just(MOCKED_TX));

        // Act & Assert
        webTestClient.post().uri("/transacciones/retiro")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .isEqualTo(MOCKED_DTO);

        verify(service).withdraw(ACC_1, AMOUNT);
    }

    // =================================================================
    // TESTS PARA TRANSFERENCIA
    // =================================================================

    @Test
    void whenPostTransfer_thenDelegatesToServiceAndReturns200() {
        // Arrange
        TransferRequestDto request = new TransferRequestDto();
        request.setOriginAccountNumber(ACC_1);
        request.setDestinationAccountNumber(ACC_2);
        request.setAmount(AMOUNT);

        when(service.transfer(anyString(), anyString(), anyDouble())).thenReturn(Mono.just(MOCKED_TX));

        // Act & Assert
        webTestClient.post().uri("/transacciones/transferencia")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .isEqualTo(MOCKED_DTO);

        verify(service).transfer(ACC_1, ACC_2, AMOUNT);
    }

    // =================================================================
    // TESTS PARA HISTORIAL (GET)
    // =================================================================

    @Test
    void whenGetHistoryWithoutParams_thenDelegatesToServiceAndReturns200() {
        // Arrange
        when(service.history(any(), any(), any(), any())).thenReturn(Flux.just(MOCKED_TX));

        // Act & Assert
        webTestClient.get().uri("/transacciones/historial")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class);

        // Verificación clave: Se llamó con todos los filtros nulos
        verify(service).history(null, null, null, null);
    }

    @Test
    void whenGetHistoryWithAllParams_thenDelegatesToServiceAndReturns200() {
        // Arrange
        LocalDate fechaDesde = LocalDate.of(2025, 1, 1);
        LocalDate fechaHasta = LocalDate.of(2025, 1, 31);
        String tipo = "DEPOSIT";

        when(service.history(anyString(), anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Flux.just(MOCKED_TX));

        // Act & Assert: Usa la forma URI builder para los parámetros GET
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/transacciones/historial")
                        .queryParam("numeroCuenta", ACC_1)
                        .queryParam("tipo", tipo)
                        .queryParam("fechaDesde", "2025-01-01")
                        .queryParam("fechaHasta", "2025-01-31")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class);

        // Verificación clave: Asegurar que el controller mapeó y pasó los parámetros correctamente
        verify(service).history(ACC_1, tipo, fechaDesde, fechaHasta);
    }
}