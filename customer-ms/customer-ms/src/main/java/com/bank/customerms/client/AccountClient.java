package com.bank.customerms.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AccountClient {

    private final RestClient restClient;

    @Value("${account.service.base-url}")
    private String baseUrl;

    /**
     * Devuelve true si el cliente tiene al menos una cuenta en account-ms
     */
    public boolean hasAccounts(Long customerId) {
        var accounts = restClient.get()
                .uri(baseUrl + "/cuentas?customerId={id}", customerId)
                .retrieve()
                .body(AccountDto[].class);
        return accounts != null && accounts.length > 0;
    }

    @Getter
    public static class AccountDto {
        private Long id;
        private String accountNumber;
        private Double balance;
        private String accountType;
        private Long customerId;
    }
}
