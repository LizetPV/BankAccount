package com.bank.service;

import com.bank.domain.*;

// define los casos de usos requeridos
public interface BankService {
    Customer registerCustomer(String firstName, String lastName, String dni, String email);
    BankAccount openAccount(String dni, AccountType type);
    void deposit(String accountNumber, double amount);
    void withdraw(String accountNumber, double amount);
    double getBalance(String accountNumber);
}
