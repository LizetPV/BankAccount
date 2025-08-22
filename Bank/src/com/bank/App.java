package com.bank;

import com.bank.domain.*;

public class App {
    public static void main(String[] args) {
        // Crear un Customer (sin validaciones por ahora)
        Customer customer = new Customer("Ana", "Garcia", "12345678", "ana@bank.com");

        // Crear cuentas y asociarlas
        BankAccount acc1 = new BankAccount("ACC001", AccountType.SAVINGS);
        BankAccount acc2 = new BankAccount("ACC002", AccountType.CHECKING);

        //asociamos las cuentas al cliente
        customer.addAccount(acc1);
        customer.addAccount(acc2);

        System.out.println("Customer: " + customer.getFirstName() + " " + customer.getLastName());
        System.out.println("DNI: " + customer.getDni() + " | Email: " + customer.getEmail());
        System.out.println("Accounts count: " + customer.getAccountsReadOnly().size());

        // Mostrar datos de la primera cuenta
        BankAccount first = customer.getAccountsReadOnly().get(0);
        System.out.println("First account -> " + first.getAccountNumber() + " (" + first.getAccountType() + ")");

        // Editar algunos campos (porque V1 es mutable)
        customer.setEmail("ana.new@bank.com");
        System.out.println("Email updated: " + customer.getEmail());

    }

}
