package com.bank.domain;

/** Entidad Cuenta Bancaria con reglas de negocio para depositar y retirar. */
public class BankAccount {

    /** Límite de sobregiro para cuentas corrientes. */
    public static final double CHECKING_OVERDRAFT_LIMIT = -500.00d;

    // ===== Atributos =====
    private String accountNumber;       //accountNumber puede venir null (el repo lo generará)
    private final String ownerDni;
    private final AccountType accountType;
    private double balance = 0.0d;   // saldo inicial 0.0

    /**
     * accountNumber puede venir null (el repo lo generará).
     * ownerDni y accountType son obligatorios (validación aquí).
     */
    public BankAccount(String accountNumber, String ownerDni, AccountType type) {
        if (ownerDni == null || ownerDni.isBlank()) throw new IllegalArgumentException("ownerDni required");
        if (type == null) throw new IllegalArgumentException("account type required");
        this.accountNumber = accountNumber;
        this.ownerDni = ownerDni;
        this.accountType = type;
    }

    /** Deposita un monto positivo. */
    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be > 0");
        balance += amount;
    }

    /**
     * Retiro con reglas:
     *  - SAVINGS: no puede quedar < 0
     *  - CHECKING: puede quedar >= -500.00; más abajo lanza excepción
     */
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