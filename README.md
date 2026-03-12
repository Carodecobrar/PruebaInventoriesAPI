# Servicio de Gestión de Inventarios

El **Servicio de Gestión de Inventarios** es un microservicio de alta concurrencia desarrollado con **Spring Boot 3.4.x** para gestionar el stock de productos de manera eficiente. Proporciona mecanismos robustos para el procesamiento de compras, asegurando la consistencia de los datos, la idempotencia y la resiliencia en integraciones externas.

---

## 🚀 Características Principales

- **Seguimiento de Inventario**: Monitoreo en tiempo real del stock disponible para cada producto.
- **Procesamiento Atómico de Compras**: Endpoint transaccional de compra con validación de stock integrada.
- **Soporte de Idempotencia**: El encabezado obligatorio `Idempotency-Key` evita el procesamiento de transacciones duplicadas, devolviendo `200 OK` para compras ya procesadas y `201 Created` para nuevas.
- **Control de Concurrencia**: Implementa **Bloqueo Optimista** (*Optimistic Locking*) utilizando versiones de entidad para manejar condiciones de carrera en escenarios de alta carga.
- **Resiliencia con Circuit Breaker**: Utiliza **Resilience4j** para proteger la comunicación con el Servicio de Productos, evitando fallos en cascada.
- **Integración Reactiva**: Valida la existencia de productos a través de un `WebClient` reactivo con tiempos de espera (*timeouts*) y lógica de reintento.
- **Rate Limiting**: Protección contra ráfagas de tráfico mediante un filtro de límite de peticiones (configurado por defecto a 60 rpm).
- **Manejo de Errores JSON:API**: Respuestas de error estandarizadas siguiendo la especificación JSON:API.
- **Migraciones de Base de Datos**: Gestionadas a través de **Flyway** para la evolución del esquema controlada por versiones.
- **Documentación de API**: Integración con **Swagger UI** para una exploración sencilla de los endpoints.

---

## 🏗️ Patrones Arquitectónicos

### 1. Idempotencia
Para evitar cobros dobles o deducciones de stock duplicadas, el servicio requiere un `Idempotency-Key` (UUID recomendado) en cada compra. El repositorio `IProcessedPurchaseRepository` rastrea las transacciones mediante esta clave.

### 2. Bloqueo Optimista (Optimistic Locking)
Utiliza la anotación `@Version` de JPA en la entidad `Inventory`. Esto asegura que si dos procesos intentan actualizar el stock del mismo producto simultáneamente, solo uno tenga éxito, mientras que el otro se reintenta (hasta 3 veces en `PurchaseService`).

### 3. Resiliencia y Cliente Reactivo
La comunicación con el Servicio de Productos externo es gestionada por `ProductsClientService` utilizando `WebClient` de Spring WebFlux e incluye:
- **Circuit Breaker**: Detecta fallos en el servicio externo y abre el circuito para evitar saturación.
- **Timeouts**: Límites de tiempo estrictos para la validación de productos.
- **Reintentos**: Intentos adicionales automáticos con retroceso exponencial.

---

## 🛠️ Stack Tecnológico

- **Java**: 21
- **Spring Boot**: 3.4.x
- **Spring Data JPA**: Capa de persistencia.
- **PostgreSQL**: Base de datos relacional.
- **Flyway**: Versionado de base de datos.
- **WebFlux**: Para llamadas HTTP reactivas.
- **Resilience4j**: Para patrones de tolerancia a fallos.
- **Lombok**: Reducción de código repetitivo.
- **SpringDoc OpenAPI**: Documentación interactiva de API (Swagger).

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
spring.datasource.username=prueba
spring.datasource.password=Prueb4
```

### Servicios Externos
Configura la URL del Servicio de Productos y la autenticación:

```properties
products.service.url=http://localhost:8080
products.service.apiKeyHeader=X-API-Key
products.service.apiKeyValue=tu-api-key-segura
```

### Ejecución de la Aplicación
```bash
./mvnw spring-boot:run
```
El servicio estará disponible en `http://localhost:8081`.

---

## 🐳 Ejecución con Docker

El proyecto incluye soporte para **Docker** y **Docker Compose**.

### Requisitos Previos
- Docker
- Red externa creada: `docker network create microservices-net` (requerida por la configuración actual).

### Pasos para ejecutar:

1. **Clonar el repositorio**.
2. **Ejecutar Docker Compose**:
   ```bash
   docker-compose up --build
   ```

Este comando levantará:
- **inventories_postgres_db**: PostgreSQL 15 en el puerto `5433`.
- **inventories_api**: La aplicación Spring Boot en el puerto `8081`.

### Configuración en Docker:
- La aplicación se conecta a la DB usando el host `inventory-db`.
- Para comunicarse con el Servicio de Productos dentro de la misma red Docker, usa el nombre del contenedor correspondiente (por defecto `products-api:8080`).

---

## 📊 Observabilidad y Logs

Se utiliza **SLF4J** con configuración para logs estructurados:
- **Logs Estructurados**: Genera logs en formato **ECS (Elastic Common Schema)** en el archivo `log.json`, optimizado para ELK Stack o Datadog.
- **Health Checks**: Endpoints de Actuator disponibles en `/actuator/health`.
- **Métricas**: Disponibles en `/actuator/metrics`.

---

## 📖 Documentación de la API

Accede a **Swagger UI**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

### Endpoint Principal: Procesar Compra
- **URL**: `POST /api/purchases`
- **Headers**:
    - `Idempotency-Key` (Obligatorio): UUID único para la transacción.
    - `Content-Type`: `application/json`
- **Cuerpo (Body)**:
  ```json
  {
    "productId": "550e8400-e29b-41d4-a716-446655440000",
    "quantity": 5
  }
  ```
- **Respuestas**:
    - `201 Created`: Compra procesada exitosamente.
    - `200 OK`: Compra ya procesada anteriormente (Idempotencia).
    - `400 Bad Request`: Error de validación o stock insuficiente.
    - `404 Not Found`: El producto no existe en el servicio externo.
    - `429 Too Many Requests`: Se ha excedido el límite de peticiones (Rate Limit).

---

## 📁 Estructura del Proyecto

- `com.prueba.inventories.controller`: Controladores REST.
- `com.prueba.inventories.service`: Lógica de negocio y clientes externos.
- `com.prueba.inventories.model`: Entidades JPA.
- `com.prueba.inventories.repository`: Repositorios de Spring Data.
- `com.prueba.inventories.dto`: DTOs, envoltorios JSON:API y excepciones.
- `com.prueba.inventories.filter`: Filtros para Rate Limiting e Idempotencia.
- `com.prueba.inventories.config`: Configuraciones de Beans y Seguridad.
- `db.migration`: Scripts SQL para Flyway.
