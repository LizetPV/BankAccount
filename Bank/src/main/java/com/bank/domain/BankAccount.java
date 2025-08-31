package com.bank.domain;

public class BankAccount {

    public static final double CHECKING_OVERDRAFT_LIMIT = -500.00d;

    // ===== Atributos =====
    private String accountNumber; // set by repository (unique)
    private final String ownerDni;
    private final AccountType accountType;
    private double balance = 0.0d;

    // ===== Constructor =====
    public BankAccount(String accountNumber, String ownerDni, AccountType type) {
        if (ownerDni == null || ownerDni.isBlank()) throw new IllegalArgumentException("ownerDni required");
        if (type == null) throw new IllegalArgumentException("account type required");
        this.accountNumber = accountNumber; // may be null initially
        this.ownerDni = ownerDni;
        this.accountType = type;
    }


    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be > 0");
        balance += amount;
    }

//metodo retirar
    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be > 0");
        double candidate = balance - amount;
        switch (accountType) {
            case SAVINGS:
                if (candidate < 0) throw new IllegalStateException("SAVINGS cannot be negative");
                break;
            case CHECKING:
                if (candidate < CHECKING_OVERDRAFT_LIMIT)
                    throw new IllegalStateException("CHECKING overdraft limit exceeded (-500.00)");
                break;
        }
        balance = candidate;
    }


    // getters & setters
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getOwnerDni() { return ownerDni; }
    public AccountType getAccountType() { return accountType; }
    public double getBalance() { return balance; }


}