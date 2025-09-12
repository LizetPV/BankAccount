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
    private String accountsBaseUrl;

    /**
     * Devuelve true si el cliente tiene al menos una cuenta en account-ms
     */
    public boolean hasAccounts(Long customerId) {
        var response = restClient.get()
                .uri(accountsBaseUrl + "/api/v1/cuentas?customerId={id}", customerId)
                .retrieve()
                // 👇 en vez de un array, deserializamos el objeto que tiene "content"
                .body(AccountPageDto.class);

        return response != null && response.getContent() != null && !response.getContent().isEmpty();
    }

    // ✅ Esta clase la dejas como está
    @Getter
    public static class AccountDto {
        private Long id;
        private String accountNumber;
        private Double balance;
        private String accountType;
        private Long customerId;
    }

    // NUEVA CLASE AQUÍ, al mismo nivel que AccountDto
    @Getter
    public static class AccountPageDto {
        private java.util.List<AccountDto> content;
        private long totalElements;
        private int totalPages;
        private int number;
        private int size;
    }
}
