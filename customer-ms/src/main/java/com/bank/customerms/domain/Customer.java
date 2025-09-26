package com.bank.customerms.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers", indexes = {
    @Index(name = "idx_customer_dni", columnList = "dni", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 80)
  private String firstName;

  @Column(nullable = false, length = 80)
  private String lastName;

  @Column(nullable = false, unique = true, length = 20)
  private String dni;

  @Column(nullable = false, length = 120)
  private String email;
}
