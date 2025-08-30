package com.bank.service;

import com.bank.domain.*;
import com.bank.repository.*;


public class BankServiceImpl implements BankService {
    private final CustomerRepository customers;
    private final BankAccountRepository accounts;


    public BankServiceImpl(CustomerRepository customers, BankAccountRepository accounts) {
        this.customers = customers; this.accounts = accounts;
    }

    /** Registra cliente después de validar DNI único y formato (valida Customer). */
    @Override
    public Customer registerCustomer(String firstName, String lastName, String dni, String email) {
        customers.findByDni(dni).ifPresent(c -> { throw new IllegalStateException("DNI already exists"); });
        var c = new Customer(firstName, lastName, dni, email);
        return customers.save(c);
    }

    /** Abre cuenta: exige cliente existente; repo asigna número; agrega cuenta al cliente. */
    @Override
    public BankAccount openAccount(String dni, AccountType type) {
        var owner = customers.findByDni(dni).orElseThrow(() -> new IllegalStateException("Customer not found"));
        var acc = new BankAccount(null, owner.getDni(), type);
        acc = accounts.save(acc); // assigns number
        owner.addAccount(acc);
        customers.save(owner);
        return acc;
    }

    /** Depósito: busca cuenta y delega en dominio (validación de monto > 0). */
    @Override
    public void deposit(String accountNumber, double amount) {
        var acc = accounts.findByNumber(accountNumber).orElseThrow(() -> new IllegalStateException("Account not found"));
        acc.deposit(amount); // regla en el dominio
        accounts.save(acc); // guardar nuevo saldo
    }

    /** Retiro: busca cuenta y delega en dominio (aplica reglas por tipo). */
    @Override
    public void withdraw(String accountNumber, double amount) {
        var acc = accounts.findByNumber(accountNumber).orElseThrow(() -> new IllegalStateException("Account not found"));
        acc.withdraw(amount);       // reglas SAVINGS/CHECKING
        accounts.save(acc);
    }

    /** Consulta de saldo. */
    @Override
    public double getBalance(String accountNumber) {
        return accounts.findByNumber(accountNumber).orElseThrow(() -> new IllegalStateException("Account not found")).getBalance();
    }
}
