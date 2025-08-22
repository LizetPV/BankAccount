package com.bank.domain;

public class BankAccount {

    // ===== Atributos =====
    private String accountNumber;   // editable en V1
    private double balance;         // inicia en 0.0
    private AccountType accountType;

    // ===== Constructor =====
    public BankAccount(String accountNumber, AccountType accountType) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = 0.0;
    }

    // ===== Getters y Setters =====
    public String getAccountNumber() { return accountNumber; }
    public double getBalance()       { return balance; }
    public AccountType getAccountType() { return accountType; }

    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }

}