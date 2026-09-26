# ---------- build stage ----------
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY auth-server/pom.xml auth-server/pom.xml
COPY product-service/pom.xml product-service/pom.xml
COPY customer-service/pom.xml customer-service/pom.xml
COPY shopping-service/pom.xml shopping-service/pom.xml
COPY assistant/pom.xml assistant/pom.xml
RUN --mount=type=cache,target=/root/.m2 chmod +x mvnw && ./mvnw -q -pl assistant -am dependency:go-offline -DskipTests || true

COPY assistant/src assistant/src
RUN --mount=type=cache,target=/root/.m2 ./mvnw -q -pl assistant -am clean package -DskipTests

# ---------- runtime stage ----------
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/assistant/target/*.jar app.jar
EXPOSE 8095
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
