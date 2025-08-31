package com.bank.service;

import com.bank.domain.*;
import com.bank.repository.*;


public class BankServiceImpl implements BankService {
    private final CustomerRepository customers;
    private final BankAccountRepository accounts;


    public BankServiceImpl(CustomerRepository customers, BankAccountRepository accounts) {
        this.customers = customers; this.accounts = accounts;
    }


    @Override
    public Customer registerCustomer(String firstName, String lastName, String dni, String email) {
        customers.findByDni(dni).ifPresent(c -> { throw new IllegalStateException("DNI already exists"); });
        var c = new Customer(firstName, lastName, dni, email);
        return customers.save(c);
    }


    @Override
    public BankAccount openAccount(String dni, AccountType type) {
        var owner = customers.findByDni(dni).orElseThrow(() -> new IllegalStateException("Customer not found"));
        var acc = new BankAccount(null, owner.getDni(), type);
        acc = accounts.save(acc); // assigns number
        owner.addAccount(acc);
        customers.save(owner);
        return acc;
    }


    @Override
    public void deposit(String accountNumber, double amount) {
        var acc = accounts.findByNumber(accountNumber).orElseThrow(() -> new IllegalStateException("Account not found"));
        acc.deposit(amount);
        accounts.save(acc);
    }


    @Override
    public void withdraw(String accountNumber, double amount) {
        var acc = accounts.findByNumber(accountNumber).orElseThrow(() -> new IllegalStateException("Account not found"));
        acc.withdraw(amount);
        accounts.save(acc);
    }


    @Override
    public double getBalance(String accountNumber) {
        return accounts.findByNumber(accountNumber).orElseThrow(() -> new IllegalStateException("Account not found")).getBalance();
    }
}
