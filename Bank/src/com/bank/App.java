package com.bank;

import com.bank.domain.*;
import com.bank.repository.memory.InMemoryBankAccountRepository;
import com.bank.repository.memory.InMemoryCustomerRepository;
import com.bank.service.BankService;
import com.bank.service.BankServiceImpl;

import java.util.Scanner;

public class App {

    private static final Scanner SC = new Scanner(System.in);
    private static BankService service;

    public static void main(String[] args) {
        service = new BankServiceImpl(new InMemoryCustomerRepository(), new InMemoryBankAccountRepository());
        System.out.println("=== BANK CONSOLE (Java 11) ===");

        boolean running = true;
        while (running) {
            printMenu();
            String opt = SC.nextLine().trim();
            try {
                switch (opt) {
                    case "1": registerCustomer(); break;
                    case "2": openAccount(); break;
                    case "3": deposit(); break;
                    case "4": withdraw(); break;
                    case "5": showBalance(); break;
                    case "0": running = false; break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.out.println(" Error: " + e.getMessage());
            }
            System.out.println();
        }
        System.out.println("Bye!");

    }

    private static void printMenu() {
        System.out.println("""
                ------------------------------
                1) Registrar cliente
                2) Abrir cuenta (SAVINGS/CHECKING)
                3) Depositar
                4) Retirar
                5) Consultar saldo
                0) Salir
                ------------------------------
                Seleccione: """);
    }

    private static void registerCustomer() {
        System.out.print("First name: ");  var first = readNonBlank();
        System.out.print("Last name: ");   var last  = readNonBlank();
        System.out.print("DNI: ");         var dni   = readNonBlank();
        System.out.print("Email: ");       var email = readNonBlank();

        var c = service.registerCustomer(first, last, dni, email);
        System.out.println(" Cliente registrado: " + c.getFirstName() + " " + c.getLastName());
    }

    private static void openAccount() {
        System.out.print("DNI del cliente: "); var dni = readNonBlank();
        System.out.print("Tipo de cuenta (SAVINGS/CHECKING): "); var typeTxt = readNonBlank().toUpperCase();

        AccountType type;
        if ("SAVINGS".equals(typeTxt)) type = AccountType.SAVINGS;
        else if ("CHECKING".equals(typeTxt)) type = AccountType.CHECKING;
        else throw new IllegalArgumentException("Tipo inválido. Use SAVINGS o CHECKING.");

        var acc = service.openAccount(dni, type);
        System.out.println(" Cuenta abierta: " + acc.getAccountNumber() + " (" + acc.getAccountType() + ")");
    }

    private static void deposit() {
        System.out.print("N° de cuenta: "); var num = readNonBlank();
        System.out.print("Monto a depositar: "); var amount = readPositiveDouble();
        service.deposit(num, amount);
        System.out.println(" Depósito Ok. Saldo: " + service.getBalance(num));
    }

    private static void withdraw() {
        System.out.print("N° de cuenta: "); var num = readNonBlank();
        System.out.print("Monto a retirar: "); var amount = readPositiveDouble();
        service.withdraw(num, amount);
        System.out.println(" Retiro Ok. Saldo: " + service.getBalance(num));
    }

    private static void showBalance() {
        System.out.print("N° de cuenta: "); var num = readNonBlank();
        System.out.println("Saldo actual: " + service.getBalance(num));
    }

    private static void listCustomerAccounts() {
        System.out.print("DNI del cliente: "); var dni = readNonBlank();
        var customer = service.registerCustomer("temp","temp",dni,"temp@temp");

        System.out.print("Cuentas (ACC..., separadas por coma): ");
        var line = SC.nextLine().trim();
        if (line.isBlank()) return;
        for (var num : line.split(",")) {
            var n = num.trim();
            try {
                System.out.println(n + " -> " + service.getBalance(n));
            } catch (Exception e) {
                System.out.println(n + " -> " + e.getMessage());
            }
        }
    }


    private static String readNonBlank() {
        var s = SC.nextLine();
        if (s == null || s.isBlank()) throw new IllegalArgumentException("Valor requerido.");
        return s.strip();
    }
    private static double readPositiveDouble() {
        var s = SC.nextLine().strip();
        double v;
        try { v = Double.parseDouble(s); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Número inválido."); }
        if (v <= 0) throw new IllegalArgumentException("El monto debe ser > 0.");
        return v;
    }
}

