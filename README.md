# youtube-microservices-spring

Código del curso **"Microservicios Modernos con Spring Boot 4 y Java 25"** — Digital Lab Academy (Eduardo Marchena).
Tienda online reconstruida desde cero: **product · customer · shopping**, con arquitectura hexagonal.

> Reemplaza al repo viejo `workshop-microservices-spring` (Spring Boot 2 / Gradle / Eureka / Config Server / Hystrix / Sleuth), que ya no compila.

## Stack

Java 25 · Spring Boot 4.0.x · Maven (multimódulo) · PostgreSQL · Docker / Compose · Testcontainers ·
(por capas) Resilience4j · Kong · OpenTelemetry · Spring Authorization Server · Kafka · Prometheus/Grafana · Spring AI.

## Decisiones de arquitectura

- **Arquitectura hexagonal** en cada servicio: `domain` (modelos, puertos, casos de uso) aislado de `infrastructure` (adaptadores REST y de persistencia). La entidad JPA **no** es el modelo de dominio.
- **Sin Eureka**: descubrimiento por **DNS de Docker** (nombres de servicio).
- **Sin Config Server**: configuración **12-Factor** (perfiles + variables de entorno).
- **Comunicación** con `RestClient` + **HTTP Interfaces** (`@HttpExchange`), sin Feign.
- **Records** de Java para modelos de dominio y DTOs; inyección por constructor; sin Lombok.

## Módulos y puertos

| Servicio | Puerto | Responsabilidad |
|---|---|---|
| product-service | 8091 | Catálogo de productos y categorías |
| customer-service | 8092 | Clientes y regiones |
| shopping-service | 8093 | Facturación; orquesta product y customer |

## Requisitos

- **JDK 25** (Temurin recomendado)
- **Docker** (para Testcontainers y Docker Compose)
- No hace falta Maven instalado: se usa el **Maven Wrapper** (`./mvnw`).

## Compilar y testear

```bash
# Apunta JAVA_HOME a tu JDK 25 (ejemplo con Temurin en ~/tools)
export JAVA_HOME=~/tools/jdk-25.0.4.1+1/Contents/Home

# Compila todo y corre los tests de integración (requiere Docker corriendo)
./mvnw clean verify
```

## Ejecutar en local (H2, sin Docker)

Cada servicio arranca con el perfil `local` (H2 en memoria) por defecto:

```bash
./mvnw -pl product-service spring-boot:run
./mvnw -pl customer-service spring-boot:run
./mvnw -pl shopping-service spring-boot:run
```

## Ejecutar todo con Docker Compose (PostgreSQL)

```bash
docker compose up --build -d
docker compose ps          # todos healthy/up

# Smoke test
curl http://localhost:8091/products
curl http://localhost:8092/customers/1
curl http://localhost:8093/invoices/1   # factura con cliente y productos resueltos

docker compose down -v     # limpiar
```

## API Gateway con Kong (ep. 9)

Kong DB-less y declarativo (`kong/kong.yml`) como puerta de entrada única, con rate limiting:

```bash
docker compose -f docker-compose.yml -f docker-compose.kong.yml up --build -d
curl http://localhost:8000/products     # a través del gateway
curl http://localhost:8001/status       # Admin API de Kong
```

## Seguridad: OAuth2 + JWT (ep. 11)

`auth-server` (Spring Authorization Server, puerto 9000) emite tokens JWT. Los tres servicios
son Resource Servers y validan la firma vía JWKS. shopping-service **propaga** el token a
product/customer (token relay).

```bash
# Token de servicio (client_credentials)
TOKEN=$(curl -s -u store-client:store-secret \
  -d grant_type=client_credentials -d scope=product.read \
  http://localhost:9000/oauth2/token | jq -r .access_token)

curl http://localhost:8091/products                 # 401 sin token
curl -H "Authorization: Bearer $TOKEN" http://localhost:8091/products   # 200
```

## Todo el sistema con un comando (ep. 14)

```bash
make up      # o: docker compose -f docker-compose.all.yml up --build -d
make ps
make token   # obtiene un JWT de servicio
make down
```

Levanta: Postgres · Auth Server · Kafka · product/customer/shopping · Kong · Jaeger · Prometheus · Grafana.

## Asistente con Spring AI (ep. 15)

Módulo `assistant` (puerto 8095): usa un modelo **Claude (Anthropic)** con Spring AI 2.0 y
herramientas (`@Tool`) que consultan el catálogo real; esas herramientas se exponen también
como **servidor MCP**. Requiere `ANTHROPIC_API_KEY`.

```bash
export ANTHROPIC_API_KEY=sk-ant-...
./mvnw -pl assistant spring-boot:run
curl -X POST http://localhost:8095/assistant/chat \
  -H 'Content-Type: application/json' \
  -d '{"message":"¿Qué zapatillas tienen y a qué precio?"}'
```

## Roadmap del curso (una rama por episodio)

`00-intro` · `01-multimodulo` · `02-hexagonal` · `03-docker` · `04-compose` · `05-testcontainers` ·
`06-config` · `07-comunicacion` · `08-resilience4j` · `09-kong` · `10-otel` · `11-security` ·
`12-saga-outbox` · `13-observabilidad` · `14-compose-full` · `15-spring-ai`

La rama `main` contiene el estado más avanzado.
