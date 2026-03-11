# Inventory Management System

Este proyecto es una microservicio desarrollado con Spring Boot para la gestión de inventarios de productos. Permite procesar compras de manera segura y consistente, validando la existencia de productos y el stock disponible.

## Características Principales

- **Gestión de Inventario**: Seguimiento de stock disponible y reservado por producto.
- **Procesamiento de Compras**: Endpoint para realizar compras con validación de stock.
- **Idempotencia**: Soporte para la cabecera `Idempotency-Key` para evitar procesamientos duplicados.
- **Optimistic Locking**: Manejo de concurrencia mediante versiones en la entidad de inventario.
- **Integración con Servicio de Productos**: Validación de productos mediante WebClient.
- **Migración de Base de Datos**: Uso de Flyway para la gestión del esquema de base de datos.
- **Documentación API**: Integración con OpenAPI (Swagger UI).

## Tecnologías Utilizadas

- Java 21
- Spring Boot 3.4.x (Configurado como 4.0.3 en el pom original)
- Spring Data JPA
- PostgreSQL
- Flyway
- SpringDoc OpenAPI
- Lombok
- Spring WebFlux (WebClient)

## Configuración y Ejecución

### Requisitos Previos

- JDK 21
- Maven 3.x
- PostgreSQL local o en contenedor

### Configuración de la Base de Datos

El servicio espera una base de datos PostgreSQL en `localhost:5433` con el nombre `inventory_db`. Puedes ajustar estas propiedades en `src/main/resources/application.properties`.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/inventory_db
spring.datasource.username=prueba
spring.datasource.password=Prueb4
```

### Ejecución

Para ejecutar la aplicación, usa el siguiente comando:

```bash
./mvnw spring-boot:run
```

El servicio estará disponible en `http://localhost:8081`.

## Documentación de la API

Una vez que la aplicación esté en ejecución, puedes acceder a la interfaz de Swagger UI en:

`http://localhost:8081/swagger-ui/index.html`

### Endpoints Principales

#### Compras

- **POST `/api/purchases`**: Procesa una nueva compra.
    - **Headers**:
        - `Idempotency-Key` (Requerido): Un identificador único para la transacción.
    - **Cuerpo (JSON)**:
      ```json
      {
        "productId": "uuid-del-producto",
        "quantity": 5
      }
      ```

## Estructura del Proyecto

- `com.prueba.inventories.controller`: Controladores REST.
- `com.prueba.inventories.service`: Lógica de negocio y clientes externos.
- `com.prueba.inventories.model`: Entidades JPA.
- `com.prueba.inventories.repository`: Interfaces de Spring Data Repository.
- `com.prueba.inventories.dto`: Objetos de transferencia de datos y excepciones.
- `com.prueba.inventories.config`: Configuraciones de beans y propiedades.
- `com.prueba.inventories.event`: Eventos internos de la aplicación.
