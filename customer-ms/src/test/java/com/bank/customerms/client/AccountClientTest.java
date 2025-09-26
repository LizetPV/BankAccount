package com.bank.customerms.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AccountClientTest {

  @Mock
  private RestClient restClient;

  @Mock
  private RestClient.RequestHeadersUriSpec<?> request;

  @Mock
  private RestClient.RequestHeadersSpec<?> headers;

  @Mock
  private RestClient.ResponseSpec responseSpec;

  @InjectMocks
  private AccountClient client;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    client = new AccountClient(restClient);
    ReflectionTestUtils.setField(client, "accountsBaseUrl", "http://localhost:8082");
  }

  @Test
  void hasAccounts_responseNull_returnsFalse() {
    doReturn(request).when(restClient).get();
    doReturn(headers).when(request).uri(anyString(), any(Object[].class));
    doReturn(responseSpec).when(headers).retrieve();
    doReturn(null).when(responseSpec).body(AccountClient.AccountPageDto.class);

    boolean result = client.hasAccounts(1L);

    assertThat(result).isFalse();
  }

  @Test
  void hasAccounts_emptyContent_returnsFalse() {
    AccountClient.AccountPageDto dto = new AccountClient.AccountPageDto();
    ReflectionTestUtils.setField(dto, "content", List.of());

    doReturn(request).when(restClient).get();
    doReturn(headers).when(request).uri(anyString(), any(Object[].class));
    doReturn(responseSpec).when(headers).retrieve();
    doReturn(dto).when(responseSpec).body(AccountClient.AccountPageDto.class);

    boolean result = client.hasAccounts(2L);

    assertThat(result).isFalse();
  }

  @Test
  void hasAccounts_withContent_returnsTrue() {
    AccountClient.AccountPageDto dto = new AccountClient.AccountPageDto();
    ReflectionTestUtils.setField(dto, "content", List.of(new AccountClient.AccountDto()));

    doReturn(request).when(restClient).get();
    doReturn(headers).when(request).uri(anyString(), any(Object[].class));
    doReturn(responseSpec).when(headers).retrieve();
    doReturn(dto).when(responseSpec).body(AccountClient.AccountPageDto.class);

    boolean result = client.hasAccounts(3L);

    assertThat(result).isTrue();
  }
}
