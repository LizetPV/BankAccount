# 🏦 Bank System - Proyecto I

Este es el primer proyecto del **Bootcamp TGP de NTTDATA**. El objetivo es desarrollar un sistema de gestión bancaria que aplique los principios de **POO**, **Diagramas UML** y **SQL Básico**.

## 🚀 Requisitos Cumplidos

  * **POO:** Implementación de las clases de dominio `Customer` y `BankAccount`.
  * **Diagramas UML:** Se presentan el Diagrama de Clases y el Diagrama de Casos de Uso.
  * **Persistencia en Memoria:** Datos de clientes y cuentas gestionados con colecciones de Java.
  * **SQL (Opcional):** Scripts y consultas para MySQL.
  * **Buenas Prácticas:** Nombres de clases y métodos en inglés, código comentado y uso de Git.

-----

## 🏗️ Estructura del Proyecto

La estructura del proyecto sigue una arquitectura de capas estándar:

```
Bank/
├── .idea/
├── src/
│   └── com/
│       └── bank/
│           ├── domain/
│           │   ├── AccountType.java
│           │   ├── BankAccount.java
│           │   └── Customer.java
│           ├── repository/
│           │   └── memory/
│           │       ├── BankAccountRepository.java
│           │       └── CustomerRepository.java
│           └── service/
│               ├── BankService.java
│               └── BankServiceImpl.java
├── .gitignore
├── Bank.iml
├── App.java
└── README.md
```

-----

## 📊 Diagramas UML

### Diagrama de Clases

Muestra la relación de composición entre `Client` y `BankAccount`. Además, muestra las relaciones de asociación y dependencia entre las otras clases

![Diagrama de Clases](https://github.com/LizetPV/BankAccount/blob/main/assets/UMLdiagrams/Diagrama%20de%20Clases.png)

### Diagrama de Casos de Uso

Ilustra las interacciones de los usuarios (`Client`) con el sistema.

![Diagrama de Casos de Uso](https://github.com/LizetPV/BankAccount/blob/main/assets/UMLdiagrams/Diagrama%20de%20Caso%20de%20Uso.png)

## Assets

Todas las **imágenes de los diagramas UML** y las **ejecuciones de las consultas SQL** se encuentran en la carpeta [`assets`](./assets):  
- `assets/UMLdiagrams` → Diagramas UML
- `assets/SQLQueryExecutions` → Ejecuciones de consultas SQL 

## Scripts

Los **scripts SQL** utilizados para crear tablas, insertar datos y realizar consultas se encuentran en la carpeta [`scripts`](./scripts).  

-----

## ⚙️ Funcionalidades Principales

  * **Registro de Clientes**
  * **Apertura de Cuentas Bancarias**
  * **Transacciones:**
      * Depósitos
      * Retiros (con reglas de sobregiro)
  * **Consulta de Saldo**

-----

## 🔒 Reglas de Negocio

  * **Clientes:** DNI único y formato de email válido.
  * **Cuentas:** Número de cuenta único, validación de cliente.
  * **Transacciones:**
      * **Ahorros:** No se permite saldo negativo.
      * **Corriente:** Límite de sobregiro de `-500.00`.

-----

## 🛠️ Tecnologías

  * **Java 8 y 11**
  * **UML**
