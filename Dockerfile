# Etapa 1: Compilación (Build)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copiar el archivo de configuración de Maven y el código fuente
COPY pom.xml .
COPY src ./src

# Compilar la librería y empaquetar el JAR saltando los tests para agilizar
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Runtime)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar el JAR generado desde la etapa de compilación
COPY --from=build /app/target/notification-lib-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

# Comando para ejecutar la clase de ejemplos
ENTRYPOINT ["java", "-jar", "app.jar"]