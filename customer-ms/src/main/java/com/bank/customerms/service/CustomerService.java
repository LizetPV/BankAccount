package com.bank.customerms.service;

import com.bank.customerms.api.dto.CustomerDtos.CustomerCreateDto;
import com.bank.customerms.api.dto.CustomerDtos.CustomerUpdateDto;
import com.bank.customerms.client.AccountClient;
import com.bank.customerms.domain.Customer;
import com.bank.customerms.domain.rule.CustomerDeletionRule; // 👈 regla SOLID (interfaz)
import com.bank.customerms.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Servicio de dominio para clientes.
 * - SRP: el servicio orquesta casos de uso;
 * la regla de eliminación vive fuera (CustomerDeletionRule).
 * - OCP: nuevas reglas se agregan con nuevas implementaciones de la interfaz,
 * sin tocar este servicio.
 * - DIP: dependemos de una abstracción (CustomerDeletionRule).
 * Mantenemos fallback con AccountClient
 *   para no romper tests existentes y
 *   permitir funcionamiento si no se define la regla como bean.
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repo;

    //  Fallback para compatibilidad (tests actuales mockean esto)
    private final AccountClient accountClient;

    //  Regla SOLID (opcional). Si hay un bean CustomerDeletionRule, lo usamos.
    //   Si no hay (p.ej., en tus tests actuales), será null y se activará el fallback.
    @Autowired(required = false)
    private CustomerDeletionRule deletionRule;

    /**
     * Crea un cliente.
     * @throws IllegalArgumentException si el DNI ya existe
     */
    @Transactional
    public Customer create(CustomerCreateDto dto) {
        var dni = dto.dni().strip();
        if (repo.existsByDni(dni)) {
            throw new IllegalArgumentException("DNI already exists");
        }
        var c = Customer.builder()
                .firstName(dto.firstName().strip())
                .lastName(dto.lastName().strip())
                .dni(dni)
                .email(dto.email().strip())
                .build();
        return repo.save(c);
    }

    /** Lista completa (no paginada). */
    public List<Customer> list() {
        return repo.findAll();
    }

    /** Obtiene un cliente o lanza 404
     * (NoSuchElementException mapeada por ControllerAdvice). */
    public Customer get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException(
            "Customer not found"));
    }

    /** Actualiza datos básicos. */
    @Transactional
    public Customer update(Long id, CustomerUpdateDto dto) {
        var c = get(id);
        c.setFirstName(dto.firstName().strip());
        c.setLastName(dto.lastName().strip());
        c.setEmail(dto.email().strip());
        return repo.save(c);
    }

    /**
     * Elimina un cliente.
     * - Si existe una CustomerDeletionRule (bean), se usa (✅ SOLID).
     * - Si no existe, se usa el fallback con AccountClient (compatibilidad con tus tests).
     * @throws IllegalStateException si el cliente tiene cuentas activas
     */
    @Transactional
    public void delete(Long id) {
        var c = get(id);

        if (deletionRule != null) {
            //  Camino SOLID (regla desacoplada)
            deletionRule.checkDeletable(id);
        } else {
            //  Fallback para compatibilidad (mismo mensaje)
            if (accountClient.hasAccounts(c.getId())) {
                throw new IllegalStateException("Customer has active accounts");
            }
        }

        repo.deleteById(id);
    }

    /** Listado paginado con búsqueda por DNI / nombre / apellido. */
    public Page<Customer> list(String q, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return repo.findAll(pageable);
        }
        var k = q.strip();
        return
            repo.findByDniContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                k, k, k, pageable
        );
    }
}
