# Etapa de construcción
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/inventories-*.jar app.jar

# Exponer el puerto configurado en application.properties
EXPOSE 8081

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
