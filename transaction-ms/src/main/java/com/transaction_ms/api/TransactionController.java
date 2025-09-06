package com.transaction_ms.api;

import com.transaction_ms.dto.*;
import com.transaction_ms.repository.model.Transaction;
import com.transaction_ms.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/transacciones")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // 1️⃣ Depósito
    @PostMapping("/deposito")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransactionDto> deposit(@RequestBody DepositRequestDto request) {
        return transactionService
                .deposit(request.getAccountNumber(), request.getAmount())
                .map(this::toDto);
    }

    // 2️⃣ Retiro
    @PostMapping("/retiro")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransactionDto> withdraw(@RequestBody WithdrawalRequestDto request) {
        return transactionService
                .withdraw(request.getAccountNumber(), request.getAmount())
                .map(this::toDto);
    }

    // 3️⃣ Transferencia
    @PostMapping("/transferencia")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransactionDto> transfer(@RequestBody TransferRequestDto request) {
        return transactionService
                .transfer(request.getOriginAccountNumber(), request.getDestinationAccountNumber(), request.getAmount())
                .map(this::toDto);
    }

    // 4️⃣ Historial (con filtros opcionales)
    @GetMapping("/historial")
    public Flux<TransactionDto> history(
            @RequestParam(required = false) String cuentaId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta
    ) {
        LocalDate desde = fechaDesde != null ? LocalDate.parse(fechaDesde) : null;
        LocalDate hasta = fechaHasta != null ? LocalDate.parse(fechaHasta) : null;

        return transactionService.history(cuentaId, tipo, desde, hasta)
                .map(this::toDto);
    }

    // 🛠️ Mapper simple: Entity → DTO
    private TransactionDto toDto(Transaction tx) {
        return TransactionDto.builder()
                .id(tx.getId())
                .type(tx.getType())
                .amount(tx.getAmount())
                .date(tx.getDate())
                .originAccountNumber(tx.getAccountFrom())
                .destinationAccountNumber(tx.getAccountTo())
                .build();
    }
}
