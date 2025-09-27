package com.bank.customerms.service;
import com.bank.customerms.api.dto.CustomerDtos.CustomerCreateDto;
import com.bank.customerms.api.dto.CustomerDtos.CustomerUpdateDto;
import com.bank.customerms.domain.Customer;
import com.bank.customerms.domain.rule.CustomerDeletionRule;
import com.bank.customerms.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository repo;

    //  ahora moqueamos la REGLA (no el AccountClient)
    @Mock
    private CustomerDeletionRule deletionRule;

    // El servicio aún recibe AccountClient por compatibilidad,
    // pero no lo necesitamos para estas pruebas.
    @Mock
    private com.bank.customerms.client.AccountClient accountClient;

    @InjectMocks
    private CustomerService service;

    private Customer customer;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        customer = Customer.builder()
                .id(1L).firstName("Ana").lastName("Lopez")
                .dni("12345678").email("ana@mail.com").build();

        // Inyectamos la regla en el field @Autowired(required=false)
        ReflectionTestUtils.setField(service, "deletionRule", deletionRule);
    }

    @Test
    void testCreateSuccess() {
        when(repo.existsByDni("12345678")).thenReturn(false);
        when(repo.save(any(Customer.class))).thenReturn(customer);

        var dto = new CustomerCreateDto("Ana", "Lopez", "12345678", "ana@mail.com");
        var saved = service.create(dto);

        assertEquals("Ana", saved.getFirstName());
        verify(repo).save(any(Customer.class));
    }

    @Test
    void testCreateFailsWhenDniExists() {
        when(repo.existsByDni("12345678")).thenReturn(true);
        var dto = new CustomerCreateDto("Ana", "Lopez", "12345678", "ana@mail.com");
        assertThrows(IllegalArgumentException.class, () -> service.create(dto));
    }

    @Test
    void testGetSuccess() {
        when(repo.findById(1L)).thenReturn(Optional.of(customer));
        var result = service.get(1L);
        assertEquals("Ana", result.getFirstName());
    }

    @Test
    void testGetNotFound() {
        when(repo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.get(2L));
    }

    @Test
    void testUpdateSuccess() {
        when(repo.findById(1L)).thenReturn(Optional.of(customer));
        when(repo.save(any(Customer.class))).thenReturn(customer);

        var dto = new CustomerUpdateDto("Maria", "Perez", "maria@mail.com");
        var updated = service.update(1L, dto);

        assertEquals("Maria", updated.getFirstName());
        assertEquals("Perez", updated.getLastName());
    }

    @Test
    void testDeleteSuccess() {
        when(repo.findById(1L)).thenReturn(Optional.of(customer));
        // la regla permite borrar (no lanza)
        doNothing().when(deletionRule).checkDeletable(1L);

        service.delete(1L);

        verify(deletionRule).checkDeletable(1L);
        verify(repo).deleteById(1L);
    }

    @Test
    void testDeleteFailsIfRuleBlocks() {
        when(repo.findById(1L)).thenReturn(Optional.of(customer));
        // la regla bloquea
        doThrow(new IllegalStateException("Customer has active accounts"))
                .when(deletionRule).checkDeletable(1L);

        assertThrows(IllegalStateException.class, () -> service.delete(1L));
        verify(deletionRule).checkDeletable(1L);
        verify(repo, never()).deleteById(anyLong());
    }

    @Test
    void testListWithoutQuery() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repo.findAll(pageable)).thenReturn(new PageImpl<>(List.of(customer)));

        var result = service.list(null, pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testListWithQuery() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repo.findByDniContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                anyString(), anyString(), anyString(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(customer)));

        var result = service.list("Ana", pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testListAll_noArgs() {
        when(repo.findAll()).thenReturn(List.of(customer));
        var result = service.list();
        assertEquals(1, result.size());
        assertEquals("Ana", result.get(0).getFirstName());
    }

    @Test
    void testListWithBlankQuery() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repo.findAll(pageable)).thenReturn(new PageImpl<>(List.of(customer)));

        var result = service.list("   ", pageable);

        assertEquals(1, result.getTotalElements());
        verify(repo).findAll(pageable);
    }
}
