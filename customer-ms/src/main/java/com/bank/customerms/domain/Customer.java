package com.bank.customerms.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "customers", uniqueConstraints = {
        @UniqueConstraint(columnNames = "dni")
})
public class Customer {

    // Identificador único
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Atributos
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "DNI is required")
    @Column(nullable = false, unique = true)
    private String dni;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    // Constructores
    protected Customer() {
        // Para JPA
    }

    public Customer(String firstName, String lastName, String dni, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dni = dni;
        this.email = email;
    }

    // Getters
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDni() { return dni; }
    public String getEmail() { return email; }

}
