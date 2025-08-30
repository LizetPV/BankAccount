package com.bank.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/** Entidad Cliente (agregado raíz). DNI es único. */
public class Customer {

    // ===== Atributos =====
    private final String firstName;
    private final String lastName;
    private final String dni; // identificador único
    private final String email; // validado por regex

    // se crea un objeto de tipo lista q va a guardar las cuentas bancarias
    private final List<BankAccount> accounts = new ArrayList<>();

//agregr una serie de validaciones para el correo
    private static final Pattern EMAIL_RX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // ===== Constructor =====
    public Customer(String firstName, String lastName, String dni, String email) {
        if (isBlank(firstName) || isBlank(lastName) || isBlank(dni) || isBlank(email)) {
            throw new IllegalArgumentException("firstName, lastName, dni, email are required");
        }
        if (!EMAIL_RX.matcher(email).matches()) {
            throw new IllegalArgumentException("invalid email format");
        }
        // Java 11: strip() quita espacios laterales
        this.firstName = firstName.strip();
        this.lastName = lastName.strip();
        this.dni = dni.strip();
        this.email = email.strip();
    }


    // ===== Métodos de relación =====
    /** Agrega una cuenta verificando que el DNI del dueño coincida. */
    public void addAccount(BankAccount account) {
        if (account == null) throw new IllegalArgumentException("account is null");
        if (!this.dni.equals(account.getOwnerDni()))
            throw new IllegalArgumentException("account owner DNI mismatch");
        accounts.add(account);
    }

    /** Vista de solo lectura para no permitir modificaciones externas. */
    public List<BankAccount> getAccountsReadOnly() { return Collections.unmodifiableList(accounts); }

    // ===== Getters / Setters (por ahora, para aprender mutabilidad) =====
    //get obtener

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDni() { return dni; }
    public String getEmail() { return email; }


    private static boolean isBlank(String s) { return s == null || s.isBlank(); }
}




