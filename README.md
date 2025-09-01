# 🏦 Bank System - Proyecto II (Microservicios)

El **Bank System** es un sistema de gestión bancaria diseñado con una arquitectura de **microservicios**, desarrollado en el marco del **Bootcamp Tech Girls Power de NTTDATA**. Este proyecto amplía las funcionalidades de gestión de clientes y cuentas bancarias para operar en un entorno distribuido, haciendo un fuerte énfasis en la **arquitectura, la documentación y las buenas prácticas de desarrollo**.

---

## Arquitectura del Sistema

La solución se compone de dos microservicios principales que se comunican para gestionar las operaciones bancarias:

* **`Customer-MS`**: Microservicio encargado de la **gestión de clientes**. Expone todos los endpoints CRUD (Crear, Leer, Actualizar, Eliminar) para los datos del cliente.
* **`Account-MS`**: Microservicio dedicado a la **gestión de cuentas bancarias** y la ejecución de transacciones, como depósitos y retiros.

Para una mejor comprensión de la arquitectura, se han incluido los siguientes diagramas en la carpeta `diagrams`:
* **[Diagrama de Componentes](https://github.com/LizetPV/BankAccount/tree/entregable2/documentation/UMLdiagrams/Diagrama%20de%20Componentes)**: Ilustra la estructura de los microservicios y sus interacciones.
* **[Diagrama de Secuencia](https://github.com/LizetPV/BankAccount/tree/entregable2/documentation/UMLdiagrams/Diagramas%20de%20Secuencias)**: Detalla el flujo de comunicación entre los servicios para operaciones clave.

---

## Tecnologías y Herramientas

* **Spring Boot**: Framework principal para el desarrollo de la API.
* **Java 11/17**: Lenguaje de programación, haciendo uso de conceptos de **Programación Funcional**.
* **OpenAPI (OpenApi Specification 3.0.3)**: Usado para documentar el contrato de la API, siguiendo un enfoque **`Contract-First`**.
* **MySQL**: Base de datos relacional para la persistencia de datos.
* **JPA / Hibernate**: Para el mapeo y la gestión de la persistencia de datos.
* **Maven**: Herramienta de gestión de dependencias.
* **Git**: Para el control de versiones del proyecto.
* **Postman**: Utilizado para las pruebas de los endpoints de la API.

---

## Endpoints de la API

Ambos microservicios exponen sus funcionalidades a través de **endpoints RESTful**. La documentación completa se encuentra en los archivos de especificación OpenAPI de cada servicio.

### **`Customer-MS` - Gestión de Clientes**

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/clientes` | Crea un nuevo cliente. |
| `GET` | `/clientes` | Lista todos los clientes registrados. |
| `GET` | `/clientes/{id}` | Obtiene los detalles de un cliente específico. |
| `PUT` | `/clientes/{id}` | Actualiza la información de un cliente. |
| `DELETE` | `/clientes/{id}` | Elimina un cliente. **Regla:** No se puede eliminar si tiene cuentas asociadas. |

### **`Account-MS` - Gestión de Cuentas**

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/cuentas` | Crea una nueva cuenta bancaria para un cliente. |
| `GET` | `/cuentas` | Lista todas las cuentas bancarias. |
| `GET` | `/cuentas/{id}` | Obtiene los detalles de una cuenta específica. |
| `PUT` | `/cuentas/{id}/depositar` | Realiza un depósito en la cuenta. |
| `PUT` | `/cuentas/{id}/retirar` | Realiza un retiro de la cuenta. **Regla:** Las cuentas de ahorro no pueden tener saldo negativo. Las cuentas corrientes permiten un sobregiro de hasta -500. |
| `DELETE` | `/cuentas/{id}` | Elimina una cuenta bancaria. |

---

## ¿Cómo ponerlo en marcha?

1.  **Clona el repositorio:**
    ```bash
    git clone https://github.com/LizetPV/BankAccount.git
    cd BankAccount
    ```
2.  **Configura la base de datos:**
    * Asegúrate de tener un servidor MySQL en funcionamiento.
    * Crea una base de datos y actualiza las credenciales de conexión en los archivos `application.properties` de `customer-ms` y `account-ms`.

3.  **Ejecuta los microservicios:**
    * Dirígete a la carpeta de cada servicio y usa Maven:
        ```bash
        cd customer-ms
        mvn spring-boot:run
        ```
    * Abre otra terminal para el segundo servicio:
        ```bash
        cd account-ms
        mvn spring-boot:run
        ```
    * Alternativamente, puedes ejecutar la aplicación directamente desde tu IDE (IntelliJ, VS Code, etc.).

4.  **Realiza las pruebas:**
    * Utiliza **Postman** para interactuar con los endpoints.
    * Los servicios se ejecutan en los siguientes puertos:
        * `Customer-MS`: **`http://localhost:8081`**
        * `Account-MS`: **`http://localhost:8082`**