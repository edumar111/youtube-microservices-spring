# ---------- build stage ----------
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# Copia del wrapper y POMs para cachear dependencias
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY product-service/pom.xml product-service/pom.xml
COPY customer-service/pom.xml customer-service/pom.xml
COPY shopping-service/pom.xml shopping-service/pom.xml
RUN --mount=type=cache,target=/root/.m2 chmod +x mvnw && ./mvnw -q -pl product-service -am dependency:go-offline -DskipTests || true

# Código fuente y empaquetado del módulo
COPY product-service/src product-service/src
RUN --mount=type=cache,target=/root/.m2 ./mvnw -q -pl product-service -am clean package -DskipTests

# ---------- runtime stage ----------
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/product-service/target/*.jar app.jar
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
