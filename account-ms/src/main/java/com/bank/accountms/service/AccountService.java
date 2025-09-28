package com.bank.accountms.service;

import com.bank.accountms.api.dto.AccountDtos.AccountCreateDto;
import com.bank.accountms.api.dto.AccountDtos.AmountDto;
import com.bank.accountms.domain.Account;
import com.bank.accountms.domain.policy.AccountPolicyFactory;
import com.bank.accountms.domain.service.AccountNumberGenerator;
import com.bank.accountms.repository.AccountRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que maneja la lógica de negocio relacionada con cuentas.
 * - Orquesta casos de uso (SRP).
 * - Reglas por tipo delegadas a Strategies (OCP).
 * - Depende de interfaces (DIP).
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    // Se mantiene por compatibilidad con tests y otros usos
    public static final double OVERDRAFT_LIMIT = -500.00;

    private final AccountRepository repo;

    // DIP: inyectamos el generador en vez de un método interno
    private final AccountNumberGenerator numberGenerator;

    /**
     * Crea una nueva cuenta bancaria.
     * @throws IllegalArgumentException si initialDeposit <= 0
     */
    @Transactional
    public Account create(AccountCreateDto dto) {
        var type = Account.AccountType.valueOf(dto.accountType().toUpperCase());
        if (dto.initialDeposit() <= 0) {
            throw new IllegalArgumentException("Initial deposit must be > 0");
        }
        var acc = Account.builder()
                .accountNumber(nextAccountNumber()) // usa generador o fallback si es null (tests)
                .balance(dto.initialDeposit())
                .accountType(type)
                .customerId(dto.customerId())
                .build();
        return repo.save(acc);
    }

    /** Listado no paginado (compatibilidad). */
    public List<Account> list(Long customerId) {
        return (customerId == null) ? repo.findAll() : repo.findByCustomerId(customerId);
    }

    /** Obtener por id o lanzar NoSuchElementException (mapeado a 404 por ControllerAdvice). */
    public Account get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Account not found"));
    }

    /** Eliminación simple (sin reglas extra aquí). */
    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    /**
     * Depósito por id.
     * 1) Valida amount primero (para que tests vean IllegalArgumentException si <= 0)
     * 2) Busca cuenta
     * 3) Strategy valida reglas
     * 4) Aplica cambios
     */
    @Transactional
    public Account deposit(Long id, AmountDto dto) {
        if (dto.amount() <= 0) { // <-- validar ANTES de tocar la BD (corrige tests)
            throw new IllegalArgumentException("Amount must be > 0");
        }
        var a = get(id);
        var policy = AccountPolicyFactory.forType(a.getAccountType());
        policy.validateDeposit(a, dto.amount()); // redundante pero ok para centralizar reglas
        a.setBalance(a.getBalance() + dto.amount());
        return repo.save(a);
    }

    /**
     * Retiro por id.
     * Valida amount primero; luego Strategy aplica reglas específicas:
     *  - Savings: no puede quedar negativo
     *  - Checking: sobregiro permitido hasta -500
     */
    @Transactional
    public Account withdraw(Long id, AmountDto dto) {
        if (dto.amount() <= 0) { // <-- validar ANTES (corrige tests)
            throw new IllegalArgumentException("Amount must be > 0");
        }
        var a = get(id);
        var policy = AccountPolicyFactory.forType(a.getAccountType());
        policy.validateWithdrawal(a, dto.amount());
        a.setBalance(a.getBalance() - dto.amount());
        return repo.save(a);
    }

    /** Suma total de saldos por cliente (demo con CF). */
    public CompletableFuture<Double> totalBalanceAsync(Long customerId) {
        var accounts = repo.findByCustomerId(customerId);
        var futures = accounts.stream()
                .map(a -> CompletableFuture.supplyAsync(a::getBalance))
                .toList();
        return CompletableFuture.supplyAsync(() ->
                futures.stream().mapToDouble(CompletableFuture::join).sum()
        );
    }

    /** Listado paginado. */
    public Page<Account> list(Long customerId, Pageable pageable) {
        return (customerId == null)
                ? repo.findAll(pageable)
                : repo.findByCustomerId(customerId, pageable);
    }

    /** Obtener por número o 404. */
    public Account getByAccountNumber(String accountNumber) {
        return repo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NoSuchElementException(
                    "Account not found: " + accountNumber));
    }

    /** Depósito por número de cuenta. */
    @Transactional
    public Account depositByNumber(String accountNumber, AmountDto dto) {
        if (dto.amount() <= 0) { // <-- validar ANTES de buscar la cuenta (corrige tests)
            throw new IllegalArgumentException("Amount must be > 0");
        }
        var acc = getByAccountNumber(accountNumber);
        var policy = AccountPolicyFactory.forType(acc.getAccountType());
        policy.validateDeposit(acc, dto.amount());
        acc.setBalance(acc.getBalance() + dto.amount());
        return repo.save(acc);
    }

    /** Retiro por número de cuenta. */
    @Transactional
    public Account withdrawByNumber(String accountNumber, AmountDto dto) {
        if (dto.amount() <= 0) { // <-- validar ANTES de buscar la cuenta (corrige tests)
            throw new IllegalArgumentException("Amount must be > 0");
        }
        var acc = getByAccountNumber(accountNumber);
        var policy = AccountPolicyFactory.forType(acc.getAccountType());
        policy.validateWithdrawal(acc, dto.amount());
        acc.setBalance(acc.getBalance() - dto.amount());
        return repo.save(acc);
    }

    // ---------- helpers ----------

    /**
     * Usa el generador inyectado; si está null (tests con @InjectMocks sin mock),
     * aplica un fallback determinista simple para no romper pruebas unitarias.
     */
    private String nextAccountNumber() {
        if (numberGenerator != null) {
            return numberGenerator.next();
        }
        // Fallback solo para entorno de prueba sin DI completa
        return "ACC" + System.nanoTime();
    }
}
