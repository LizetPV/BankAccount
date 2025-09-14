package com.bank.customerms;

import com.bank.customerms.api.dto.CustomerDtos.CustomerCreateDto;
import com.bank.customerms.api.dto.CustomerDtos.CustomerUpdateDto;
import com.bank.customerms.client.AccountClient;
import com.bank.customerms.domain.Customer;
import com.bank.customerms.repository.CustomerRepository;
import com.bank.customerms.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repo;

    @Mock
    private AccountClient accountClient;

    @InjectMocks
    private CustomerService service;

    private Customer existing;
    private Customer saved;

    @BeforeEach
    void init() {
        existing = Customer.builder()
                .id(1L)
                .firstName("Ana")
                .lastName("Perez")
                .dni("12345678")
                .email("ana@bank.com")
                .build();

        saved = Customer.builder()
                .id(2L)
                .firstName("Luis")
                .lastName("Lopez")
                .dni("87654321")
                .email("luis@bank.com")
                .build();
    }

    @Test
    @DisplayName("create: guarda cliente cuando DNI no existe (AAA)")
    void create_ok_whenDniNotExists() {
        // Arrange
        var dto = new CustomerCreateDto("  Luis ", " Lopez ", " 87654321 ", " luis@bank.com ");
        when(repo.existsByDni(anyString())).thenReturn(false);
        // Verificamos que se persiste con valores 'strip'
        when(repo.save(any(Customer.class))).thenReturn(saved);

        // Act
        Customer result = service.create(dto);

        // Assert
        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        var captor = ArgumentCaptor.forClass(Customer.class);
        verify(repo).save(captor.capture());
        Customer toSave = captor.getValue();
        assertEquals("Luis", toSave.getFirstName());
        assertEquals("Lopez", toSave.getLastName());
        assertEquals("87654321", toSave.getDni());
        assertEquals("luis@bank.com", toSave.getEmail());
        verify(repo).existsByDni("87654321");
    }

    @Test
    @DisplayName("create: lanza IllegalArgumentException si DNI ya existe")
    void create_throws_whenDniExists() {
        // Arrange
        var dto = new CustomerCreateDto("Luis", "Lopez", "87654321", "luis@bank.com");
        when(repo.existsByDni(anyString())).thenReturn(true);

        // Act + Assert
        var ex = assertThrows(IllegalArgumentException.class, () -> service.create(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("dni"));
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("get: devuelve cliente por id")
    void get_ok() {
        // Arrange
        when(repo.findById(1L)).thenReturn(Optional.of(existing));

        // Act
        Customer result = service.get(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repo).findById(1L);
    }

    @Test
    @DisplayName("get: lanza NoSuchElementException si no existe")
    void get_throws_whenNotFound() {
        // Arrange
        when(repo.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(NoSuchElementException.class, () -> service.get(99L));
        verify(repo).findById(99L);
    }

    @Test
    @DisplayName("update: actualiza campos y guarda")
    void update_ok() {
        // Arrange
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        var dto = new CustomerUpdateDto("  Ana María ", "  Pérez  ", " ana.new@bank.com ");

        // Act
        Customer updated = service.update(1L, dto);

        // Assert
        assertEquals("Ana María", updated.getFirstName());
        assertEquals("Pérez", updated.getLastName());
        assertEquals("ana.new@bank.com", updated.getEmail());
        verify(repo).save(existing);
    }

    @Test
    @DisplayName("delete: si tiene cuentas activas -> IllegalStateException y no borra")
    void delete_throws_whenHasAccounts() {
        // Arrange
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(accountClient.hasAccounts(1L)).thenReturn(true);

        // Act + Assert
        assertThrows(IllegalStateException.class, () -> service.delete(1L));
        verify(repo, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("delete: si NO tiene cuentas activas -> borra por id")
    void delete_ok_whenNoAccounts() {
        // Arrange
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(accountClient.hasAccounts(1L)).thenReturn(false);

        // Act
        service.delete(1L);

        // Assert
        verify(repo).deleteById(1L);
    }

    @Test
    @DisplayName("list(): sin filtro llama repo.findAll(pageable)")
    void list_noQuery_callsFindAll() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());
        Page<Customer> page = new PageImpl<>(List.of(existing), pageable, 1);
        when(repo.findAll(pageable)).thenReturn(page);

        // Act
        Page<Customer> result = service.list(null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(repo).findAll(pageable);
        verify(repo, never())
            .findByDniContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                anyString(), anyString(), anyString(), any());
    }

    @Test
    @DisplayName("list(q): con filtro usa búsqueda por dni/first/last (ignora espacios)")
    void list_withQuery_callsSearch() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> page = new PageImpl<>(List.of(existing), pageable, 1);
        when(repo.findByDniContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                "ana", "ana", "ana", pageable)).thenReturn(page);

        // Act
        Page<Customer> result = service.list("  ana  ", pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(repo)
            .findByDniContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                "ana", "ana", "ana", pageable);
        verify(repo, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("list(): devuelve todos los clientes (repo.findAll())")
    void list_all_returnsAll() {
        // Arrange
        when(repo.findAll()).thenReturn(List.of(existing, saved));

        // Act
        var result = service.list();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(existing.getId(), result.get(0).getId());
        assertEquals(saved.getId(), result.get(1).getId());
        verify(repo).findAll();
    }

}
