# 🏦 Bank System - Proyecto III (Microservicios Escalables)

El **Bank System - Proyecto III** es la evolución del sistema bancario desarrollado en el **Bootcamp Tech Girls Power de NTTDATA**, esta vez con un enfoque en **escalabilidad, programación reactiva y bases de datos NoSQL**.  

El objetivo es implementar la **gestión de transacciones bancarias** (depósitos, retiros y transferencias) bajo una arquitectura de **microservicios**, utilizando **Spring WebFlux** y **MongoDB** para garantizar un sistema eficiente, reactivo y altamente escalable.

---

## 📂 Documentación

Dentro de la carpeta [`documentation`](./documentation) se incluyen los artefactos del proyecto:  

### UMLdiagrams

#### Diagrama de Componentes
- [Diagrama de Componentes - Transacciones.png](./documentation/UMLdiagrams/Diagrama%20de%20Componentes/Diagrama%20de%20Componentes%20-%20Transacciones.png)

#### Diagramas de Secuencias
- [Consulta de historial de transacciones - Diagrama de secuencia.png](./documentation/UMLdiagrams/Diagramas%20de%20Secuencias/Consulta%20de%20historial%20de%20transacciones-%20Diagrama%20de%20secuencia.png)
- [Registro de Depósito - Diagrama de secuencia.png](./documentation/UMLdiagrams/Diagramas%20de%20Secuencias/Registro%20de%20Deposito%20-%20Diagrama%20de%20secuencia.png)
- [Registro de Retiro - Diagrama de secuencia.png](./documentation/UMLdiagrams/Diagramas%20de%20Secuencias/Registro%20de%20Retiro%20-%20Diagrama%20de%20secuencia.png)
- [Registro de Transferencia - Diagrama de secuencia.png](./documentation/UMLdiagrams/Diagramas%20de%20Secuencias/Registro%20de%20Transferencia%20-%20Diagrama%20de%20secuencia.png)

---

## ⚙️ Tecnologías y Herramientas

- **Spring Boot** → Framework base de los microservicios.  
- **Java 11/17** → Lenguaje principal, aplicando **POO, programación funcional y reactiva**.  
- **Spring WebFlux** → Para desarrollo reactivo y manejo de peticiones no bloqueantes.  
- **MySQL** → Base de datos relacional para el microservicio de cuentas (`account-ms`).  
- **MongoDB** → Base de datos NoSQL para el microservicio de transacciones (`transaction-ms`).  
- **Lombok** → Para reducir código repetitivo (constructores, getters/setters, builders).  
- **OpenAPI (Swagger 3.0.3)** → Documentación de la API bajo enfoque **Contract-First**.  
- **Maven** → Gestión de dependencias.  
- **Git** → Control de versiones.  
- **Postman** → Validación de los endpoints.  

---

## 📌 Endpoints de la API

### `Transaction-MS` - Gestión de Transacciones

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/transacciones/deposito` | Registra un depósito en una cuenta. |
| `POST` | `/transacciones/retiro` | Registra un retiro de una cuenta (con validación de saldo). |
| `POST` | `/transacciones/transferencia` | Registra una transferencia entre dos cuentas. |
| `GET` | `/transacciones/historial` | Consulta el historial completo de transacciones. |

---

## 📋 Reglas de Negocio

- Los depósitos y retiros se aplican a **cuentas existentes** en `Account-MS`.  
- Las transferencias requieren **cuenta de origen, cuenta destino y monto**.  
- No se permiten retiros ni transferencias si el **saldo disponible es insuficiente**.  
- Cada transacción se registra en MongoDB como un **documento con información completa**:  
  - Tipo (Depósito, Retiro, Transferencia)  
  - Monto  
  - Fecha  
  - Cuenta de origen y cuenta de destino (si aplica)  

---

## ▶️ ¿Cómo ponerlo en marcha?

Para iniciar el proyecto, sigue estos sencillos pasos:

1.  **Clona el repositorio** en tu máquina local. Abre la terminal o el símbolo del sistema y ejecuta el siguiente comando:

    ```bash
    git clone  https://github.com/LizetPV/BankAccount.git
    ```

2.  **Inicia el servicio `account-ms`**. Este microservicio se ejecuta en el puerto **8082** y utiliza una base de datos **MySQL**. Asegúrate de que tu instancia de MySQL esté en funcionamiento y luego arranca el servicio.

3.  **Inicia el servicio `transactions-ms`**. Este microservicio se ejecuta en el puerto **8083** y utiliza **MongoDB**. Verifica que tu instancia de MongoDB esté activa y luego pon en marcha este servicio.

4.  **Realiza las pruebas:**
    * Utiliza **Postman** para interactuar con los endpoints. En esta documentación se encuentra el archivo json de la [colección](/documentation/Entregable%203%20-%20Bank%20System.postman_collection.json).
    * Los servicios se ejecutan en los siguientes puertos:
        * `Account-MS`: **`http://localhost:8082`**
        * `Transacction-MS`: **`http://localhost:8083`**
