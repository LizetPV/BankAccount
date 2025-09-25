package com.bank.customerms.client;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;

class RestClientConfigTest {

  @Test
  void testRestClientBeanCreation() {
    RestClientConfig config = new RestClientConfig();
    RestClient client = config.restClient(RestClient.builder());
    assertNotNull(client);
  }
}
