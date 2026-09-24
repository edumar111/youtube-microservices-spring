# Plan de implementación — youtube-microservices-spring

> Documento de referencia de la migración del curso "Microservicios Modernos con Spring Boot 4
> y Java 25" (Digital Lab Academy). Registra el contexto, las decisiones, lo implementado por
> episodio, los problemas resueltos y cómo verificar y ejecutar todo.
> Fecha de la migración: 26 sept 2026.

---

## 1. Contexto y objetivo

El repo viejo `workshop-microservices-spring` (curso 2020) usaba **Gradle + jcenter, Spring Boot 2.2.5,
Java 11, Eureka + Config Server + Hystrix + Sleuth + Feign**, con entidades JPA usadas como dominio
(`@Data`), `@Autowired` en campos, `javax.persistence` y dos bugs reales. Ya no compila y contradice
las decisiones del curso 2026.

**Objetivo:** reconstruir desde cero la tienda online (product, customer, shopping) en un repo nuevo
**`youtube-microservices-spring`**, cumpliendo el temario 2026 y las decisiones canónicas del README §5
(sin Kubernetes, sin Eureka, sin Config Server, sin Keycloak, con Kong).

**Versiones verificadas (sept 2026):** Java **25** (LTS) · Spring Boot **4.0.8** · Spring Cloud
2025.1.x "Oakwood" (no necesaria al final) · Spring AI **2.0.1**.

**Bugs del curso viejo corregidos:**
- `InvoiceServiceImpl.getInvoice()` usaba `item.getId()` → ahora usa `item.getProductId()`.
- `createInvoice()` descontaba stock por HTTP dentro de una transacción local (inconsistencia) →
  resuelto con **Saga + Outbox sobre Kafka** (ep. 12).

**Decisiones tomadas al inicio (con el autor):**
1. Instalar JDK 25 + Maven Wrapper y **verificar los builds** (no solo generar código).
2. **Núcleo primero** (ep. 1–7), luego las capas (ep. 8–15).
3. Git con `main` + **una rama por episodio**.

---

## 2. Arquitectura

Cada servicio sigue **arquitectura hexagonal** (puertos y adaptadores):

```
academy.digitallab.onlinestore.<svc>
├── domain
│   ├── model            → modelos de dominio puros (records, SIN JPA)
│   ├── port/in          → casos de uso (interfaces)
│   ├── port/out         → puertos de salida (repos, clientes)
│   ├── service          → implementación de casos de uso (lógica de negocio)
│   ├── event            → eventos de dominio (ep. 12)
│   └── exception        → excepciones de dominio
└── infrastructure
    ├── adapter/in/web           → @RestController + DTOs + mappers
    ├── adapter/out/persistence  → @Entity JPA + Spring Data + adaptador + mapper
    ├── adapter/out/client       → HTTP Interfaces (RestClient) hacia otros servicios
    ├── messaging                → productores/consumidores Kafka (ep. 12)
    └── config                   → wiring, seguridad, clientes HTTP
```

La entidad JPA está **separada** del modelo de dominio (responde al comentario del curso viejo sobre
acoplar JPA con el dominio). Inyección por constructor, records y virtual threads; **sin Lombok**.

### Módulos y puertos

| Módulo | Puerto | Responsabilidad |
|---|---|---|
| auth-server | 9000 | Spring Authorization Server (emite JWT) |
| product-service | 8091 | Catálogo de productos y categorías |
| customer-service | 8092 | Clientes y regiones |
| shopping-service | 8093 | Facturación; orquesta product y customer |
| assistant | 8095 | Asistente de compras (Spring AI) |
| (infra) | | PostgreSQL 5432 · Kong 8000/8001 · Kafka 9092 · Jaeger 16686 · Prometheus 9090 · Grafana 3000 |

---

## 3. Fase A — Núcleo (ep. 1–7)

- **Ep. 1 — Multimódulo:** POM padre (`spring-boot-starter-parent:4.0.8`, `java.version=25`), Maven Wrapper 3.9.9.
- **Ep. 2 — Hexagonal:** product/customer/shopping con dominio aislado, entidad JPA aparte, records.
- **Ep. 3–4 — Docker/Compose:** Dockerfile multi-stage (JDK 25 → JRE) por servicio; `docker-compose.yml`
  con PostgreSQL (una BD por servicio, `docker/init-databases.sql`), healthchecks y variables de entorno.
- **Ep. 5 — Testcontainers:** tests de integración con PostgreSQL real (`@ServiceConnection`).
- **Ep. 6 — Config 12-Factor:** perfiles `local` (H2) y `postgres`; variables de entorno; sin Config Server.
- **Ep. 7 — Comunicación:** shopping → product/customer con **RestClient + HTTP Interfaces** (`@HttpExchange`),
  descubrimiento por DNS de Docker (sin Eureka, sin Feign). Aquí se corrige el bug de `getProductId`.

**Verificado end-to-end** con `docker compose up`: catálogo servido, factura creada descontando stock
entre servicios por DNS, y factura enriquecida con cliente + productos (fix confirmado).

---

## 4. Fase B — Capas (ep. 8–15)

- **Ep. 8 — Resilience4j:** circuit breaker + retry + timeout + fallback en los adaptadores de cliente de
  shopping. Orden de aspectos: `@Retry` externo con `fallbackMethod`, `@CircuitBreaker` interno. Timeouts en
  el RestClient. Endpoints actuator de circuit breakers.
- **Ep. 9 — Kong:** `kong/kong.yml` DB-less declarativo (rutas + rate-limiting + correlation-id);
  `docker-compose.kong.yml` (proxy 8000, admin 8001). Sustituye al gateway-service viejo.
- **Ep. 10 — OpenTelemetry:** `micrometer-tracing-bridge-otel` + `opentelemetry-exporter-otlp` en los 3
  servicios; Jaeger en `docker-compose.observability.yml`.
- **Ep. 11 — Seguridad:** módulo `auth-server` (Spring Authorization Server); cliente `store-client`
  (client_credentials y authorization_code); los 3 servicios como Resource Servers (validación JWT por JWKS);
  shopping propaga el Bearer token a product/customer (token relay).
- **Ep. 12 — Saga + Outbox con Kafka:** shopping deja la factura en `PENDING` y escribe `InvoiceCreated` en
  la tabla `outbox_event` **en la misma transacción**; un relay `@Scheduled` publica a Kafka; product consume,
  descuenta stock y publica `StockProcessed`; shopping confirma (`CONFIRMED`) o compensa (`CANCELLED`).
  Elimina el descuento síncrono del ep. 7.
- **Ep. 13 — Observabilidad:** `micrometer-registry-prometheus` + `/actuator/prometheus`; Prometheus con
  reglas de alerta (`InstanceDown`, `HighHttpServerErrors`) y Grafana con datasource y dashboard provisionados.
- **Ep. 14 — Compose total:** `docker-compose.all.yml` (vía `include`) + `Makefile` → un comando levanta los 10 servicios.
- **Ep. 15 — Spring AI:** módulo `assistant` con modelo Claude (Anthropic), herramientas `@Tool` que consultan
  el catálogo real, expuestas también como **servidor MCP**. Endpoint `POST /assistant/chat`.

---

## 5. Ramas (una por episodio)

`main` contiene el estado más avanzado. Ramas: `01-multimodulo` · `02-hexagonal` · `03-docker` ·
`04-compose` · `05-testcontainers` · `06-config` · `07-comunicacion` · `08-resilience4j` · `09-kong` ·
`10-otel` · `11-security` · `12-saga-outbox` · `13-observabilidad` · `14-compose-full` · `15-spring-ai`.

---

## 6. Gotchas de Spring Boot 4 (resueltos durante la migración)

| Tema | Detalle |
|---|---|
| **Jackson 3** | Paquete `tools.jackson.databind` (no `com.fasterxml.jackson`). Excepciones no chequeadas. |
| **Kafka** | La autoconfig vive en `spring-boot-starter-kafka` (no basta `spring-kafka`). |
| **Testcontainers 2.0** | Artefactos renombrados a `testcontainers-<módulo>` (junit-jupiter, postgresql, kafka). |
| **@ServiceConnection Kafka** | No soporta `ConfluentKafkaContainer` → usar `@DynamicPropertySource` con `getBootstrapServers()`. |
| **KafkaContainer apache** | `apache/kafka` KRaft en Testcontainers puede fallar el wait strategy; se usó Confluent + DynamicPropertySource. |
| **Spring Security 7** | Sin `applyDefaultSecurity`; SAS con `new OAuth2AuthorizationServerConfigurer()` + `http.with(...)`. Clases en `spring-security-config`. |
| **MockMvc test** | `@AutoConfigureMockMvc` ahora en `org.springframework.boot.webmvc.test.autoconfigure` (dep `spring-boot-webmvc-test`). |
| **AOP** | No existe `spring-boot-starter-aop`; usar `org.aspectj:aspectjweaver`. |
| **Semillas SQL** | Sin IDs explícitos en `data.sql` (la secuencia IDENTITY debe avanzar para no colisionar al crear). |

---

## 7. Verificación

Toolchain: **JDK 25** (instalado sin sudo en `~/tools/jdk-25.0.4.1+1`) y Docker.

```bash
export JAVA_HOME=~/tools/jdk-25.0.4.1+1/Contents/Home
export DOCKER_CONFIG=/tmp/empty-docker-config   # evita cuelgues del credential helper

./mvnw clean verify        # 6 módulos, 15 tests (Testcontainers: PostgreSQL y Kafka reales)
```

Tests que cubren lo esencial:
- product: seeds, updateStock, stock insuficiente (409), seguridad web (401/200), **saga Kafka** (descuenta stock).
- customer: seed cliente, alta con estado CREATED.
- shopping: enriquecido por `productId` (fix), **outbox escrito en create**, **fallback de Resilience4j**.

Smoke test end-to-end (Docker):

```bash
make up                       # o docker compose -f docker-compose.all.yml up --build -d
TOKEN=$(make token | jq -r .access_token)
curl -H "Authorization: Bearer $TOKEN" http://localhost:8000/products   # vía Kong
make down
```

UIs: Jaeger `:16686` · Prometheus `:9090` · Grafana `:3000` (admin/admin) · Kong admin `:8001`.

---

## 8. Cómo ejecutar (resumen)

```bash
make build     # compila + tests
make up        # levanta todo (Postgres, Auth, Kafka, servicios, Kong, observabilidad)
make ps
make token     # JWT de servicio (client_credentials)
make logs
make down

# Asistente (ep. 15) — requiere clave
export ANTHROPIC_API_KEY=sk-ant-...
./mvnw -pl assistant spring-boot:run
```

---

## 9. Stack

Java 25 · Spring Boot 4.0.8 · Maven (multimódulo) · PostgreSQL · Docker / Compose · Testcontainers ·
arquitectura hexagonal · Resilience4j · Kong · OpenTelemetry (Jaeger) · Spring Authorization Server ·
Kafka (Saga + Outbox) · Prometheus / Grafana · Spring AI (Claude + MCP).
