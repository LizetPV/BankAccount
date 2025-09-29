# 🚀 Proyecto BankAccount – Microservicios

Este proyecto fue desarrollado en el **Bootcamp NTT DATA Tech Girls Power**, siguiendo un enfoque incremental a lo largo de cinco entregables.  
El objetivo fue construir un ecosistema bancario compuesto por microservicios independientes, aplicando **principios SOLID**, **patrones de diseño**, **programación funcional y reactiva**, además de **buenas prácticas de calidad** (Jacoco, Checkstyle, Mockito).

---

## 🏛 Arquitectura de Microservicios

### 👤 Customer-MS (MySQL + Spring Boot REST)  
- CRUD completo de clientes.  
- Validación de DNI único.  
- Restricción: no se permite eliminar clientes con cuentas activas (consulta vía **AccountClient**).  

### 🏦 Account-MS (MySQL + Spring Boot REST)  
- Creación de cuenta con número único (**AccountNumberGenerator**).  
- Depósitos y retiros con reglas según tipo de cuenta (**Savings / Checking**).  
- Límite de sobregiro hasta **-500** en cuentas corrientes.  
- Cálculo de saldo total por cliente.  
- Endpoints accesibles por **ID** y por **accountNumber**.  

### 💳 Transaction-MS (MongoDB + WebFlux)  
- Operaciones: **Depósito, Retiro, Transferencia**.  
- Registro de transacciones en **MongoDB**.  
- Historial accesible con filtros: número de cuenta, tipo, fechas.  

---

## ⚙️ Tecnologías Utilizadas

- **Lenguaje & Frameworks:** Java 17, Spring Boot 3.5.x (REST + WebFlux)  
- **Bases de Datos:** MySQL (Customer, Account), MongoDB (Transaction)  
- **Documentación & Contratos:** OpenAPI / Swagger (*Contract-First*)  
- **Otras librerías:** Lombok, JPA/Hibernate  

### 🧪 Pruebas & Calidad
- ✅ **JUnit 5 + Mockito** (tests unitarios y reactivos)  
- ✅ **Jacoco** (cobertura de código)  
- ✅ **Checkstyle** (estilo de código)  

---

## ✅ Endpoints Principales

### 👤 Clientes
- `POST /clientes` – Crear cliente  
- `GET /clientes` – Listar clientes  
- `GET /clientes/{id}` – Obtener cliente por ID  
- `PUT /clientes/{id}` – Actualizar cliente  
- `DELETE /clientes/{id}` – Eliminar cliente (solo si no tiene cuentas)  

### 🏦 Cuentas
- `POST /cuentas` – Crear cuenta para cliente  
- `GET /cuentas` – Listar cuentas (paginadas)  
- `GET /cuentas/{id}` – Obtener cuenta por ID  
- `GET /cuentas/numero/{accountNumber}` – Obtener cuenta por número  
- `PUT /cuentas/{id}/depositar` – Depositar por ID  
- `PUT /cuentas/{id}/retirar` – Retirar por ID  
- `PUT /cuentas/numero/{accountNumber}/depositar` – Depositar por número  
- `PUT /cuentas/numero/{accountNumber}/retirar` – Retirar por número  
- `GET /cuentas/total/{customerId}` – Consultar saldo total por cliente  

### 💳 Transacciones
- `POST /transacciones/deposito` – Registrar depósito  
- `POST /transacciones/retiro` – Registrar retiro  
- `POST /transacciones/transferencia` – Registrar transferencia  
- `GET /transacciones/historial` – Consultar historial con filtros  

---

## 🧑‍💻 Buenas Prácticas Aplicadas

### 📑 Contract First con OpenAPI
- Los contratos `.yaml` definen los modelos y endpoints antes de la implementación.  

### 📌 Principios SOLID
- **SRP:** Separación de responsabilidades en servicios, políticas y generadores.  
- **OCP:** Uso de **AccountPolicyFactory** para extender reglas sin modificar código base.  
- **DIP:** Inyección de dependencias (ejemplo: **AccountNumberGenerator**).  

### 🏗 Patrones de Diseño
- **Strategy**  
- **Factory**  

### 🛡 Calidad de Código
- ✔️ Cobertura de pruebas con **Jacoco**  
- ✔️ Estilo validado con **Checkstyle**  
- ✔️ Tests unitarios y reactivos con **JUnit + Mockito**  
