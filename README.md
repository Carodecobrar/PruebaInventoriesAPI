# Inventory Management Service

The **Inventory Management Service** is a high-concurrency microservice developed with **Spring Boot 3.x (configured as 4.0.3 in the POM)** to manage product stock efficiently. It provides robust mechanisms for processing purchases, ensuring data consistency and idempotency.

---

## 🚀 Key Features

- **Inventory Tracking**: Real-time monitoring of available and reserved stock per product.
- **Atomic Purchase Processing**: Transactional purchase endpoint with built-in stock validation.
- **Idempotency Support**: Mandatory `Idempotency-Key` header prevents duplicate transaction processing.
- **Concurrency Control**: Implements **Optimistic Locking** using entity versioning to handle race conditions in high-load scenarios.
- **Resilient External Integration**: Validates product existence via a reactive `WebClient` with built-in timeouts and retry logic.
- **Event-Driven Design**: Dispatches `InventoryChangedEvent` internally after successful transactions.
- **JSON:API Error Handling**: Standardized error responses following the JSON:API specification.
- **Database Migrations**: Managed via **Flyway** for version-controlled schema evolution.
- **API Documentation**: Integrated **Swagger UI** for easy endpoint exploration.

---

## 🏗️ Architectural Patterns

### 1. Idempotency
To prevent double-billing or duplicate stock deduction, the service requires an `Idempotency-Key` for every purchase. The `IProcessedPurchaseRepository` tracks successful transactions by this key.

### 2. Optimistic Locking
Uses the JPA `@Version` annotation on the `Inventory` entity. This ensures that if two processes attempt to update the same product's stock simultaneously, only one succeeds, while the other is retried (up to 3 times in `PurchaseService`).

### 3. Reactive Client
Communication with the external Products Service is handled by `ProductsClientService` using Spring WebFlux's `WebClient`. It includes:
- **Timeouts**: 3-second limit for product validation.
- **Retries**: 3 attempts with exponential backoff (1s initial delay).

---

## 🛠️ Tech Stack

- **Java**: 21
- **Spring Boot**: 3.4.x (configured with parent 4.0.3)
- **Spring Data JPA**: Persistence layer.
- **PostgreSQL**: Relational database.
- **Flyway**: Database versioning.
- **WebFlux**: For reactive HTTP client calls.
- **Lombok**: Boilerplate reduction.
- **SpringDoc OpenAPI**: Interactive API documentation.

---

## ⚙️ Configuration & Execution

### Prerequisites
- **JDK 21**
- **Maven 3.x**
- **PostgreSQL** instance (accessible at `localhost:5433`).

### Database Setup
The service expects a database named `inventory_db`. Configure your credentials in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/inventory_db
spring.datasource.username=your_user
spring.datasource.password=your_password
```

### External Services
Configure the Products Service URL and authentication in `application.properties`:

```properties
api.products.url=http://external-products-service/api
api.key.header=X-API-Key
api.key.value=your-secure-api-key
```

### Running the Application
```bash
./mvnw spring-boot:run
```
The service will be available at `http://localhost:8081`.

---

## 📖 API Documentation

Access the **Swagger UI** for full interactive documentation:
[http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

### Primary Endpoint: Process Purchase
- **URL**: `POST /api/purchases`
- **Headers**:
    - `Idempotency-Key` (Required): Unique transaction identifier (UUID recommended).
- **Body**:
  ```json
  {
    "productId": "550e8400-e29b-41d4-a716-446655440000",
    "quantity": 5
  }
  ```

---

## 📁 Project Structure

- `com.prueba.inventories.controller`: REST API controllers.
- `com.prueba.inventories.service`: Business logic, retry mechanisms, and external client services.
- `com.prueba.inventories.model`: JPA entities (Inventory, ProcessedPurchase).
- `com.prueba.inventories.repository`: Spring Data JPA repositories.
- `com.prueba.inventories.dto`: Data transfer objects, JSON:API wrappers, and custom exceptions.
- `com.prueba.inventories.config`: Spring configuration beans (WebClient, Custom properties).
- `com.prueba.inventories.event`: Internal application events for decoupled processing.
- `db.migration`: Flyway SQL migration scripts.
