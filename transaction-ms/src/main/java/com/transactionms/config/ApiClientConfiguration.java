package com.transactionms.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.transactionms.client.account.ApiClient;
import com.transactionms.client.account.api.CuentasApi;

@Configuration
public class ApiClientConfiguration {

    @Value("${application.apiclient.notification.uri}")
    private String baseUri;
    @Value("${application.apiclient.notification.timeout}")
    private Long timeout;

    @Bean
    ApiClient apiClient() {
        ApiClient client = new ApiClient();
        client.updateBaseUri(baseUri);
        client.setConnectTimeout(Duration.ofMillis(timeout));
        return client;
    }

    @Bean
    CuentasApi cuentasApi(ApiClient apiClient) {
        return new CuentasApi(apiClient);
    }

}