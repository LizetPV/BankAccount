package com.transactionms.client;

import com.transactionms.client.dto.AccountDto;
import com.transactionms.client.dto.AmountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AccountClient {

    private final WebClient webClient;

    public Mono<AccountDto> getAccount(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(AccountDto.class);
    }

    public Mono<AccountDto> deposit(Long id, Double amount) {
        return webClient.put()
                .uri("/{id}/depositar", id)
                .bodyValue(new AmountDto(amount))
                .retrieve()
                .bodyToMono(AccountDto.class);
    }

    public Mono<AccountDto> withdraw(Long id, Double amount) {
        return webClient.put()
                .uri("/{id}/retirar", id)
                .bodyValue(new AmountDto(amount))
                .retrieve()
                .bodyToMono(AccountDto.class);
    }

    public Mono<AccountDto> getByAccountNumber(String accountNumber) {
        return webClient.get()
                .uri("/ByNumber/{accountNumber}", accountNumber)
                .retrieve()
                .bodyToMono(AccountDto.class);
    }

    public Mono<AccountDto> depositByNumberAccount(String accountNumber, Double amount) {
        return webClient.put()
                .uri("/ByNumber/{accountNumber}/depositar", accountNumber)
                .bodyValue(new AmountDto(amount))
                .retrieve()
                .bodyToMono(AccountDto.class);
    }

    public Mono<AccountDto> withdrawByAccountNumber(String accountNumber, Double amount) {
        return webClient.put()
                .uri("/ByNumber/{accountNumber}/retirar", accountNumber)
                .bodyValue(new AmountDto(amount))
                .retrieve()
                .bodyToMono(AccountDto.class);
    }
}
