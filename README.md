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
