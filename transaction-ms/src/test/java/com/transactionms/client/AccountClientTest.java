package com.transactionms.client;


import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

/**
 * AAA + FIRST + KISS: cada test cubre un endpoint del cliente con MockWebServer (sin red real).
 */
class AccountClientTest {

    static MockWebServer server;
    AccountClient client;

    @BeforeAll
    static void start() throws Exception {
        server = new MockWebServer();
        server.start();
    }

    @AfterAll
    static void stop() throws Exception {
        server.shutdown();
    }

    @BeforeEach
    void setUp() {
        String baseUrl = server.url("/").toString();
        client = new AccountClient(WebClient.builder().baseUrl(baseUrl).build());
    }

    @Test
    void getAccount_returnsAccount() {
        // Arrange
        server.enqueue(json(200, """
          {"id":1,"accountNumber":"ACC-1","balance":1000.0,"accountType":"SAVINGS","customerId":10}
        """));
        // Act & Assert
        StepVerifier.create(client.getAccount(1L))
                .expectNextMatches(a -> a.getId()==1 && a.getBalance()==1000.0)
                .verifyComplete();
    }

    @Test
    void deposit_updatesBalance() {
        // Arrange
        server.enqueue(json(200, """
          {"id":1,"accountNumber":"ACC-1","balance":1100.0,"accountType":"SAVINGS","customerId":10}
        """));
        // Act & Assert
        StepVerifier.create(client.deposit(1L, 100.0))
                .expectNextMatches(a -> a.getBalance()==1100.0)
                .verifyComplete();
    }

    @Test
    void withdraw_updatesBalance() {
        // Arrange
        server.enqueue(json(200, """
          {"id":1,"accountNumber":"ACC-1","balance":900.0,"accountType":"SAVINGS","customerId":10}
        """));
        // Act & Assert
        StepVerifier.create(client.withdraw(1L, 100.0))
                .expectNextMatches(a -> a.getBalance()==900.0)
                .verifyComplete();
    }

    @Test
    void getByAccountNumber_ok() {
        // Arrange
        server.enqueue(json(200, """
          {"id":2,"accountNumber":"ACC-2","balance":500.0,"accountType":"CHECKING","customerId":11}
        """));
        // Act & Assert
        StepVerifier.create(client.getByAccountNumber("ACC-2"))
                .expectNextMatches(a -> "ACC-2".equals(a.getAccountNumber()))
                .verifyComplete();
    }

    @Test
    void depositByNumberAccount_ok() {
        // Arrange
        server.enqueue(json(200, """
          {"id":2,"accountNumber":"ACC-2","balance":700.0,"accountType":"CHECKING","customerId":11}
        """));
        // Act & Assert
        StepVerifier.create(client.depositByNumberAccount("ACC-2", 200.0))
                .expectNextMatches(a -> a.getBalance()==700.0)
                .verifyComplete();
    }

    @Test
    void withdrawByAccountNumber_ok() {
        // Arrange
        server.enqueue(json(200, """
          {"id":2,"accountNumber":"ACC-2","balance":300.0,"accountType":"CHECKING","customerId":11}
        """));
        // Act & Assert
        StepVerifier.create(client.withdrawByAccountNumber("ACC-2", 200.0))
                .expectNextMatches(a -> a.getBalance()==300.0)
                .verifyComplete();
    }

    // --- Helpers (DRY + KISS) ---
    private static MockResponse json(int code, String body) {
        return new MockResponse()
                .setResponseCode(code)
                .addHeader("Content-Type", "application/json")
                .setBody(body);
    }
}
