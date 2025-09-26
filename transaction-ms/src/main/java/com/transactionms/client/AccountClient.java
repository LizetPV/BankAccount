package com.transactionms.client;

import com.transactionms.client.dto.AccountDto;
import com.transactionms.client.dto.AmountDto;
import com.transactionms.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Implementación concreta del servicio de cuentas usando WebClient.
 * Cumple con Dependency Inversion Principle (DIP) - implementa abstracción AccountService.
 * 
 * @deprecated Use AccountServiceImpl with OpenAPI client instead
 */
@Component("webClientAccountService")
@RequiredArgsConstructor
public class AccountClient implements AccountService {

    private final WebClient webClient;

    @Override
    public Mono<AccountDto> getAccount(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(AccountDto.class);
    }

    @Override
    public Mono<AccountDto> deposit(Long id, Double amount) {
        return webClient.put()
                .uri("/{id}/depositar", id)
                .bodyValue(new AmountDto(amount))
                .retrieve()
                .bodyToMono(AccountDto.class);
    }

    @Override
    public Mono<AccountDto> withdraw(Long id, Double amount) {
        return webClient.put()
                .uri("/{id}/retirar", id)
                .bodyValue(new AmountDto(amount))
                .retrieve()
                .bodyToMono(AccountDto.class);
    }

    @Override
    public Mono<AccountDto> getByAccountNumber(String accountNumber) {
        return webClient.get()
                .uri("/ByNumber/{accountNumber}", accountNumber)
                .retrieve()
                .bodyToMono(AccountDto.class);
    }

    @Override
    public Mono<AccountDto> depositByAccountNumber(String accountNumber, Double amount) {
        return webClient.put()
                .uri("/ByNumber/{accountNumber}/depositar", accountNumber)
                .bodyValue(new AmountDto(amount))
                .retrieve()
                .bodyToMono(AccountDto.class);
    }

    @Override
    public Mono<AccountDto> withdrawByAccountNumber(String accountNumber, Double amount) {
        return webClient.put()
                .uri("/ByNumber/{accountNumber}/retirar", accountNumber)
                .bodyValue(new AmountDto(amount))
                .retrieve()
                .bodyToMono(AccountDto.class);
    }
}
