# youtube-microservices-spring

Código del curso **"Microservicios Modernos con Spring Boot 4 y Java 25"** — Digital Lab Academy (Eduardo Marchena).
Tienda online (product · customer · shopping) con arquitectura hexagonal, más un **asistente IA** (Spring AI)
y un **frontend Angular**.

- **Backend (este repo):** https://github.com/edumar111/youtube-microservices-spring
- **Frontend (Angular):** https://github.com/edumar111/youtube-frontend-angular

**Stack:** Java 25 · Spring Boot 4.0.x · Maven · PostgreSQL · Docker/Compose · Testcontainers ·
Resilience4j · Kong · OpenTelemetry (Jaeger) · Spring Authorization Server · Kafka (Saga/Outbox) ·
Prometheus/Grafana · Spring AI (Claude + MCP) · Angular 22.

---

## 🚀 Levantar TODO el proyecto (paso a paso)

### 0) Requisitos
- **JDK 25** (Temurin). Exporta `JAVA_HOME` a tu JDK 25 (solo para compilar/tests; no hace falta para Docker).
- **Docker** (Docker Desktop) corriendo.
- **Node 22** (para el frontend). Con nvm: `nvm install 22 && nvm use 22`.
- No necesitas Maven ni Angular CLI instalados: se usan `./mvnw` y la CLI local del frontend.

### 1) Backend completo (microservicios + infraestructura)
Levanta Postgres, Authorization Server, Kafka, product/customer/shopping, Kong, Jaeger, Prometheus y Grafana:
```bash
cd youtube-microservices-spring
make up
# equivale a: docker compose -f docker-compose.all.yml up --build -d
make ps          # ver estado
```
> Primera vez: construye las imágenes (compila cada servicio dentro de su contenedor), tarda varios minutos.

### 2) Asistente IA (opcional — ep. 15)
El asistente (Spring AI + Claude) es un **extra** que necesita una API key. Configúralo con un `.env`:
```bash
cp .env.example .env
# edita .env:  LLM_ENABLED=true   LLM_MODEL=claude-haiku-4-5   ANTHROPIC_API_KEY=sk-ant-...
docker compose -f docker-compose.all.yml -f docker-compose.assistant.yml up --build -d assistant
```
- `LLM_ENABLED=false` (por defecto) → el asistente arranca **sin clave** y el chat responde "desactivado".
- `LLM_MODEL` → modelo de Anthropic; **Haiku es el más económico**.
- El `.env` va en la raíz de este repo y **no se sube** (está en `.gitignore`).

### 3) Frontend (Angular)
En **otra terminal**, clona/entra al repo del frontend y arráncalo:
```bash
git clone https://github.com/edumar111/youtube-frontend-angular
cd youtube-frontend-angular
nvm use 22
npm install
npm start                 # http://localhost:4200
```

### 4) Probar la tienda
Abre **http://localhost:4200**:
1. **Navega y busca** el catálogo y **agrega al carrito** — todo **anónimo** (sin login).
2. Pulsa **"Ir a pagar"** → te pedirá **iniciar sesión** (login estilizado del Authorization Server).
3. Tras pagar verás la factura pasar de **PENDING → CONFIRMED** (saga con Kafka).
4. Si activaste el asistente, usa la **burbuja de chat** (abajo a la derecha) para preguntar por el catálogo.

**Credenciales de demo:** usuario `user` · contraseña `password`.

### 5) Apagar todo
```bash
# backend (+ asistente si lo levantaste)
docker compose -f docker-compose.all.yml -f docker-compose.assistant.yml down -v
# frontend: Ctrl+C en la terminal de 'npm start'
```

---

## 🌐 URLs y puertos

| Servicio | URL | Notas |
|---|---|---|
| Frontend (Angular) | http://localhost:4200 | tienda |
| API Gateway (Kong) | http://localhost:8000 | entrada única a la API |
| Kong Admin | http://localhost:8001 | administración de Kong |
| Authorization Server | http://localhost:9000 | login OIDC (`/login`) |
| product-service | http://localhost:8091 | catálogo (GET público) |
| customer-service | http://localhost:8092 | clientes |
| shopping-service | http://localhost:8093 | facturación (saga) |
| assistant (IA) | http://localhost:8095 | `/assistant/chat` (opcional) |
| Jaeger (trazas) | http://localhost:16686 | OpenTelemetry |
| Prometheus | http://localhost:9090 | métricas |
| Grafana | http://localhost:3000 | dashboards (admin/admin) |
| PostgreSQL | localhost:5432 | store/store |
| Kafka | localhost:9092 | saga/outbox |

**Token de servicio (client_credentials):** `make token`

---

## ✅ Compilar y ejecutar los tests

```bash
export JAVA_HOME=<ruta-a-tu-JDK-25>
./mvnw clean verify        # 6 módulos, tests de integración con Testcontainers (requiere Docker)
```

## Ejecutar un servicio en local (sin Docker, perfil H2)
```bash
./mvnw -pl product-service spring-boot:run     # H2 en memoria, sin dependencias
```

---

## 🧭 Arquitectura (resumen)

Cada microservicio usa **arquitectura hexagonal**: `domain` (modelos, puertos, casos de uso) aislado de
`infrastructure` (adaptadores REST y de persistencia). Decisiones canónicas: **sin Eureka** (DNS de Docker),
**sin Config Server** (config 12-Factor), **sin Feign** (RestClient + HTTP Interfaces), **sin Keycloak**
(Spring Authorization Server), **API Gateway = Kong**.

| Módulo | Puerto | Responsabilidad |
|---|---|---|
| auth-server | 9000 | Emite JWT (OAuth2/OIDC) |
| product-service | 8091 | Catálogo (catálogo público; escrituras protegidas) |
| customer-service | 8092 | Clientes y regiones |
| shopping-service | 8093 | Facturación; saga + outbox |
| assistant | 8095 | Asistente IA (Spring AI, opcional) |

## 🌿 Ramas por episodio
`main` es el estado más avanzado. Hay una rama por episodio: `01-multimodulo` … `15-spring-ai` y `16-frontend`.

## 📚 Documentación
Ver la carpeta [`docs/`](docs/):
- `PLAN-IMPLEMENTACION.md` — plan completo de la migración (ep. 1–15).
- `TDD-vs-TESTCONTAINERS.md` — tests: unitarios vs integración; TDD vs Testcontainers.
- `EP15-ASISTENTE-SPRING-AI.md` — asistente IA: Tool Calling, MCP, RAG, widget y configuración.

## 🛠️ Solución de problemas
- **`make up` falla al construir:** asegúrate de que Docker está corriendo y hay espacio en disco.
- **Testcontainers no encuentra Docker / cuelgues:** exporta `DOCKER_CONFIG=/tmp/empty-docker-config` para evitar el credential helper.
- **El frontend no arranca:** usa Node 22 (`nvm use 22`); la CLI de Angular exige `^22.22.3 || ^24.15.0`.
- **El chat del asistente dice "no disponible":** falta levantar el servicio `assistant` (paso 2). Si lo levantaste **después** de Kong y ves "name resolution failed", **reinicia Kong** (`docker compose -f docker-compose.all.yml restart kong`) para que re-resuelva el DNS. Si `LLM_ENABLED=false`, el chat responde "desactivado" (es correcto).
- **El catálogo no carga en la web:** revisa que Kong (`:8000`) y product-service estén arriba (`make ps`).
