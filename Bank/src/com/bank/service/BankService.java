package com.bank.service;

import com.bank.domain.*;


public interface BankService {
    Customer registerCustomer(String firstName, String lastName, String dni, String email);
    BankAccount openAccount(String dni, AccountType type);
    void deposit(String accountNumber, double amount);
    void withdraw(String accountNumber, double amount);
    double getBalance(String accountNumber);
}
