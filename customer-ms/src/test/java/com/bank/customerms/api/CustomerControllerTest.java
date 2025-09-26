package com.bank.customerms.api;

import com.bank.customerms.contract.model.CustomerCreateDto;
import com.bank.customerms.contract.model.CustomerDto;
import com.bank.customerms.contract.model.CustomerPage;
import com.bank.customerms.contract.model.CustomerUpdateDto;
import com.bank.customerms.domain.Customer;
import com.bank.customerms.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

  @Mock
  private CustomerService service;

  @InjectMocks
  private CustomerController controller;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void listCustomers_defaults() {
    Customer c = Customer.builder().id(1L).firstName("Ana").lastName("Torres")
        .dni("123").email("a@a.com").build();
    Page<Customer> page = new PageImpl<>(List.of(c));
    when(service.list(eq(null), any(Pageable.class))).thenReturn(page);

    ResponseEntity<?> resp = controller.listCustomers(null, null, null, null);

    assertThat(resp.getStatusCodeValue()).isEqualTo(200);
    verify(service).list(eq(null), any(Pageable.class));
  }

  @Test
  void listCustomers_withSortAsc() {
    when(service.list(eq("q"), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    ResponseEntity<?> resp = controller.listCustomers("q", 1, 5, "firstName,asc");

    assertThat(resp.getStatusCodeValue()).isEqualTo(200);
  }

  @Test
  void listCustomers_invalidSort_usesDefault() {
    when(service.list(eq("q"), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    ResponseEntity<?> resp = controller.listCustomers("q", 0, 10, "badSortString");

    assertThat(resp.getStatusCodeValue()).isEqualTo(200);
  }

  @Test
  void listCustomers_sortCausesException_fallsBackToDefault() {
    Customer customer = Customer.builder()
        .id(1L)
        .firstName("Ana")
        .lastName("Lopez")
        .dni("12345678")
        .email("ana@mail.com")
        .build();

    Page<Customer> page = new PageImpl<>(List.of(customer));
    when(service.list(any(), any(Pageable.class))).thenReturn(page);

    // 🔥 Forzamos excepción: el split devuelve array vacío → parts[0] falla
    ResponseEntity<CustomerPage> response =
        controller.listCustomers(null, 0, 10, ",");

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getSort()).isEqualTo("id: DESC");
  }




  @Test
  void createCustomer_ok() {
    CustomerCreateDto dto = new CustomerCreateDto();
    dto.setFirstName("Ana");
    dto.setLastName("Torres");
    dto.setDni("123");
    dto.setEmail("a@a.com");

    Customer created = Customer.builder().id(1L).firstName("Ana").lastName("Torres")
        .dni("123").email("a@a.com").build();

    when(service.create(any())).thenReturn(created);

    ResponseEntity<CustomerDto> resp = controller.createCustomer(dto);

    assertThat(resp.getStatusCodeValue()).isEqualTo(201);
    assertThat(resp.getBody().getId()).isEqualTo(1L);
  }

  @Test
  void getCustomer_ok() {
    Customer c = Customer.builder().id(2L).firstName("Ana").lastName("Torres")
        .dni("123").email("a@a.com").build();
    when(service.get(2L)).thenReturn(c);

    ResponseEntity<CustomerDto> resp = controller.getCustomer(2L);

    assertThat(resp.getBody().getId()).isEqualTo(2L);
  }

  @Test
  void updateCustomer_ok() {
    CustomerUpdateDto dto = new CustomerUpdateDto();
    dto.setFirstName("Ana");
    dto.setLastName("Torres");
    dto.setEmail("a@a.com");

    Customer updated = Customer.builder().id(3L).firstName("Ana").lastName("Torres")
        .dni("123").email("a@a.com").build();
    when(service.update(eq(3L), any())).thenReturn(updated);

    ResponseEntity<CustomerDto> resp = controller.updateCustomer(3L, dto);

    assertThat(resp.getBody().getId()).isEqualTo(3L);
  }

  @Test
  void deleteCustomer_ok() {
    doNothing().when(service).delete(5L);

    ResponseEntity<Void> resp = controller.deleteCustomer(5L);

    assertThat(resp.getStatusCodeValue()).isEqualTo(204);
    verify(service).delete(5L);
  }
}
