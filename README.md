# Servicio de Gestión de Inventarios

El **Servicio de Gestión de Inventarios** es un microservicio de alta concurrencia desarrollado con **Spring Boot 3.x (configurado como 4.0.3 en el POM)** para gestionar el stock de productos de manera eficiente. Proporciona mecanismos robustos para el procesamiento de compras, asegurando la consistencia de los datos y la idempotencia.

---

## 🚀 Características Principales

- **Seguimiento de Inventario**: Monitoreo en tiempo real del stock disponible y reservado por producto.
- **Procesamiento Atómico de Compras**: Endpoint transaccional de compra con validación de stock integrada.
- **Soporte de Idempotencia**: El encabezado obligatorio `Idempotency-Key` evita el procesamiento de transacciones duplicadas.
- **Control de Concurrencia**: Implementa **Bloqueo Optimista** (*Optimistic Locking*) utilizando versiones de entidad para manejar condiciones de carrera en escenarios de alta carga.
- **Integración Resiliente**: Valida la existencia de productos a través de un `WebClient` reactivo con tiempos de espera (*timeouts*) y lógica de reintento integrados.
- **Diseño Orientado a Eventos**: Despacha un `InventoryChangedEvent` internamente después de transacciones exitosas.
- **Manejo de Errores JSON:API**: Respuestas de error estandarizadas siguiendo la especificación JSON:API.
- **Migraciones de Base de Datos**: Gestionadas a través de **Flyway** para la evolución del esquema controlada por versiones.
- **Documentación de API**: Integración con **Swagger UI** para una exploración sencilla de los endpoints.

---

## 🏗️ Patrones Arquitectónicos

### 1. Idempotencia
Para evitar cobros dobles o deducciones de stock duplicadas, el servicio requiere un `Idempotency-Key` para cada compra. El repositorio `IProcessedPurchaseRepository` rastrea las transacciones exitosas mediante esta clave.

### 2. Bloqueo Optimista (Optimistic Locking)
Utiliza la anotación `@Version` de JPA en la entidad `Inventory`. Esto asegura que si dos procesos intentan actualizar el stock del mismo producto simultáneamente, solo uno tenga éxito, mientras que el otro se reintenta (hasta 3 veces en `PurchaseService`).

### 3. Cliente Reactivo
La comunicación con el Servicio de Productos externo es gestionada por `ProductsClientService` utilizando `WebClient` de Spring WebFlux. Incluye:
- **Timeouts**: Límite de 3 segundos para la validación de productos.
- **Reintentos**: 3 intentos con retroceso exponencial (*exponential backoff*, retraso inicial de 1s).

---

## 🛠️ Stack Tecnológico

- **Java**: 21
- **Spring Boot**: 3.4.x (configurado con parent 4.0.3)
- **Spring Data JPA**: Capa de persistencia.
- **PostgreSQL**: Base de datos relacional.
- **Flyway**: Versionado de base de datos.
- **WebFlux**: Para llamadas HTTP reactivas.
- **Lombok**: Reducción de código repetitivo.
- **SpringDoc OpenAPI**: Documentación interactiva de API.

---

## ⚙️ Configuración y Ejecución

### Requisitos Previos
- **JDK 21**
- **Maven 3.x**
- **Instancia de PostgreSQL** (accesible en `localhost:5433`).

### Configuración de la Base de Datos
El servicio espera una base de datos llamada `inventory_db`. Configura tus credenciales en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/inventory_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

### Servicios Externos
Configura la URL del Servicio de Productos y la autenticación en `application.properties`:

```properties
api.products.url=http://servicio-externo-productos/api
api.key.header=X-API-Key
api.key.value=tu-api-key-segura
```

### Ejecución de la Aplicación
```bash
./mvnw spring-boot:run
```
El servicio estará disponible en `http://localhost:8081`.

---

## 📖 Documentación de la API

Accede a **Swagger UI** para la documentación interactiva completa:
[http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

### Endpoint Principal: Procesar Compra
- **URL**: `POST /api/purchases`
- **Encabezados**:
    - `Idempotency-Key` (Obligatorio): Identificador único de transacción (se recomienda UUID).
- **Cuerpo (Body)**:
  ```json
  {
    "productId": "550e8400-e29b-41d4-a716-446655440000",
    "quantity": 5
  }
  ```

---

## 📁 Estructura del Proyecto

- `com.prueba.inventories.controller`: Controladores de la API REST.
- `com.prueba.inventories.service`: Lógica de negocio, mecanismos de reintento y servicios de clientes externos.
- `com.prueba.inventories.model`: Entidades JPA (Inventory, ProcessedPurchase).
- `com.prueba.inventories.repository`: Repositorios de Spring Data JPA.
- `com.prueba.inventories.dto`: Objetos de transferencia de datos, envoltorios JSON:API y excepciones personalizadas.
- `com.prueba.inventories.config`: Beans de configuración de Spring (WebClient, propiedades personalizadas).
- `com.prueba.inventories.event`: Eventos internos de la aplicación para procesamiento desacoplado.
- `db.migration`: Scripts de migración SQL de Flyway.
