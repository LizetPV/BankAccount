package com.transactionms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigTests {

  @Test
  void testMongoConfigBeans() {
    ReactiveMongoDatabaseFactory factory = mock(ReactiveMongoDatabaseFactory.class);
    MongoConfig config = new MongoConfig();

    ReactiveMongoTransactionManager txManager = config.transactionManager(factory);
    assertNotNull(txManager);

    TransactionalOperator operator = config.transactionalOperator(txManager);
    assertNotNull(operator);
  }

  @Test
  void testOpenApiConfigBeans() {
    OpenApiConfig config = new OpenApiConfig();

    OpenAPI openAPI = config.transactionOpenAPI();
    assertNotNull(openAPI);
    assertEquals("Transaction API", openAPI.getInfo().getTitle());
    assertEquals("1.0.0", openAPI.getInfo().getVersion());
    assertTrue(openAPI.getInfo().getDescription().contains("depósitos"));

    GroupedOpenApi groupedOpenApi = config.publicApi();
    assertNotNull(groupedOpenApi);
  }

  @Test
  void testUtilityConfigClockBean() {
    UtilityConfig config = new UtilityConfig();
    Clock clock = config.clock();

    assertNotNull(clock);
    assertEquals(Clock.systemUTC().getZone(), clock.getZone());
  }

  @Test
  void testWebClientConfigBean() {
    WebClientConfig config = new WebClientConfig();
    String testUrl = "http://localhost:8080";
    try {
      var field = WebClientConfig.class.getDeclaredField("accountServiceUrl");
      field.setAccessible(true);
      field.set(config, testUrl);
    } catch (Exception e) {
      fail("No se pudo inyectar el valor en accountServiceUrl");
    }

    WebClient client = config.webClient(WebClient.builder());
    assertNotNull(client);
  }
}
