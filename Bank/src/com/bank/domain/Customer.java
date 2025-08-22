package com.bank.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Customer {

    // ===== Atributos =====
    private String firstName;
    private String lastName;
    private String dni;
    private String email;

    // se crea un objeto de tipo lista q va a guardar las cuentas bancarias
    private final List<BankAccount> accounts = new ArrayList<>();

    // ===== Constructor =====
    public Customer(String firstName, String lastName, String dni, String email) {
        this.firstName = firstName;
        this.lastName  = lastName;
        this.dni       = dni;
        this.email     = email;
    }


    // ===== Métodos de relación =====
    //método void agregar cuenta
    public void addAccount(BankAccount account) {
        if (account == null) throw new IllegalArgumentException("account cannot be null");
        accounts.add(account);
    }

    //metodo mostrar la lista de cuentas
    public List<BankAccount> getAccountsReadOnly() {
        return Collections.unmodifiableList(accounts);
    }

    // ===== Getters / Setters (por ahora, para aprender mutabilidad) =====
    //get obtener
    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }
    public String getDni()       { return dni; }
    public String getEmail()     { return email; }

    //set asigna valor
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName)   { this.lastName  = lastName;  }
    public void setDni(String dni)             { this.dni       = dni;       }
    public void setEmail(String email)         { this.email     = email;     }


}

