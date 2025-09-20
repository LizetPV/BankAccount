package com.transactionms.controller;

import com.transactionms.dto.DepositRequestDto;
import com.transactionms.dto.TransactionDto;
import com.transactionms.dto.TransferRequestDto;
import com.transactionms.dto.WithdrawalRequestDto;
import com.transactionms.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/transacciones")
@Tag(name = "Transacciones", description = "Endpoints para registrar y consultar transacciones")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @Operation(summary = "Registrar un depósito")
    @PostMapping("/deposito")
    public Mono<TransactionDto> deposit(@RequestBody DepositRequestDto request) {
        return service.deposit(request.getAccountNumber(), request.getAmount())
                .map(TransactionDto::fromEntity);
    }

    @Operation(summary = "Registrar un retiro")
    @PostMapping("/retiro")
    public Mono<TransactionDto> withdraw(@RequestBody WithdrawalRequestDto request) {
        return service.withdraw(request.getAccountNumber(), request.getAmount())
                .map(TransactionDto::fromEntity);
    }

    @Operation(summary = "Registrar una transferencia")
    @PostMapping("/transferencia")
    public Mono<TransactionDto> transfer(@RequestBody TransferRequestDto request) {
        return service.transfer(request.getOriginAccountNumber(),
                request.getDestinationAccountNumber(), request.getAmount())
                .map(TransactionDto::fromEntity);
    }

    @Operation(summary = "Consultar historial de transacciones")
    @GetMapping("/historial")
    public Flux<TransactionDto> history(
            @RequestParam(required = false) String numeroCuenta,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        return service.history(numeroCuenta, tipo, fechaDesde, fechaHasta)
                .map(TransactionDto::fromEntity);
    }
}

