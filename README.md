# 🏦 Bank System - Proyecto IV

## 📌 Descripción

Este proyecto forma parte del **Proyecto IV**, el cual extiende las funcionalidades desarrolladas en los entregables anteriores. El contexto es un sistema bancario, incorporando buenas prácticas, patrones de diseño y herramientas de calidad de código.

---

## 🚀 Funcionalidades obligatorias

* [x] Pruebas unitarias con **JUnit** y **Mockito** para los microservicios *Clientes* y *Cuentas*.
* [x] Reporte de cobertura de código con **Jacoco**.
* [x] Aplicación de **Checkstyle**.
* [x] Evaluación y documentación de **principios SOLID** y **patrones de diseño** aplicados.

---

## 📂 Evidencias de calidad

Las evidencias generadas se encuentran en la carpeta [`/documentation/reports`](./documentation/reports):

### 📊 Coverage Reports (JaCoCo)

* ![Account Jacoco Report](./documentation/reports/jacoco/Account-Jacoco.png)
* ![Customer Jacoco Report](./documentation/reports/jacoco/Customer-Jacoco.png)

### 📝 Checkstyle Reports

* [`account-checkstyle-result.xml`](./documentation/reports/checkstyle/account-heckstyle-result.xml)
* [`customer-checkstyle-result.xml`](./documentation/reports/checkstyle/customer-checkstyle-result.xml)

### ✅ Unit Test Results

* ![Account Test Results](./documentation/reports/test/account-test.png)
* ![Customer Test Results](./documentation/reports/test/customer-test.png)

---
## 📌 Instrucciones de ejecución de reportes

### ▶️ Ejecutar pruebas unitarias con cobertura (Jacoco)

Desde el microservicio correspondiente (ej. *account-ms* o *customer-ms*):

```bash
mvn clean verify
```

Los reportes se generarán en `target/site/jacoco/`.

### ▶️ Ejecutar Checkstyle

```bash
mvn checkstyle:checkstyle
```

El reporte se generará en `target/site/checkstyle.html` o en formato XML dentro de `target/`.

### ▶️ Ejecutar pruebas unitarias (JUnit/Mockito)

```bash
mvn test
```

Los resultados se mostrarán en la consola y en `target/surefire-reports/`.

---

## ✅ Principios SOLID

### S - _Single Responsibility Principle (Principio de Responsabilidad Única)_

Tanto en el microservicio **Account** como en el **Customer**, cada clase cumple un único propósito:

- **Controllers (`AccountController.java`, `CustomerController.java`)** → exponen los endpoints REST y reciben las solicitudes HTTP.
- **Services (`AccountService.java`, `CustomerService.java`)** → implementan la lógica de negocio.
- **Repositories (`AccountRepository.java`, `CustomerRepository.java`)** → encapsulan el acceso a la base de datos.
- **Entities (`Account.java`, `Customer.java`)** → representan las entidades del dominio *accountms* y *customerms*.
- **`RestExceptionHandler.java`** → gestiona de manera aislada las excepciones que puedan ocurrir en la capa API.

Esto asegura que cada clase tenga **una sola razón de cambio**, favoreciendo la mantenibilidad y evitando la mezcla de responsabilidades.


### O - *Open/Closed Principle (Principio Abierto/Cerrado)*

El código debe estar **abierto a la extensión**, pero **cerrado a la modificación**. En los microservicios esto se refleja en:

* **Uso de interfaces en los repositorios (ej. `AccountRepository`, `CustomerRepository`)**: se definen como interfaces que heredan de [`JpaRepository`](https://docs.spring.io/spring-data/data-jpa/docs/current/api/org/springframework/data/jpa/repository/JpaRepository.html), lo cual permite extender funcionalidades sin modificar el código existente.
* **Servicios (`AccountService`, `CustomerService`)** → implementan la lógica de negocio. Ejemplo: en `AccountRepository`, al extender `JpaRepository<Account, Long>`, se heredan métodos como `findAll()`, `findById()`, `save()`, lo que permite demostrar cómo se cumple el principio OCP sin reescribirlos. lógica puede extenderse agregando nuevos métodos o implementaciones sin alterar los ya definidos.
* **Controladores**: se pueden añadir nuevos endpoints para ampliar la funcionalidad, sin modificar los existentes, manteniendo la compatibilidad.
* **Patrones aplicables**: por ejemplo, en el código `public interface AccountRepository extends JpaRepository<Account, Long>` se demuestra claramente la herencia de `JpaRepository`; además se puede aplicar el patrón **Strategy** o **Template Method** para extender comportamientos de negocio en operaciones recurrentes (por ejemplo, validaciones o reglas de retiro y depósito), evitando tocar código base.

Este enfoque asegura que el sistema pueda **evolucionar sin riesgo de romper funcionalidades previas**, alineándose con el principio de mantenibilidad.

---
