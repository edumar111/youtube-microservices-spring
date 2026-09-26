# Ep. 15 — Asistente de compras con Spring AI (Tool Calling, MCP y RAG)

> Explica qué es el asistente, qué son **MCP** y **RAG**, qué se implementó exactamente en este
> proyecto, cómo probarlo y cómo se añadiría RAG. Módulo: `assistant/` (puerto **8095**).

---

## 1. Qué es el asistente

Un microservicio (`assistant`) con **Spring AI 2.0** que expone un chat de compras. El usuario
pregunta en lenguaje natural (“¿qué zapatillas tienen y a qué precio?”) y el modelo **Claude
(Anthropic)** responde consultando **datos reales** del catálogo a través de los microservicios,
en vez de inventar productos o precios.

La clave es cómo le damos al modelo acceso a esos datos reales. Hay dos técnicas habituales:
**Tool Calling / MCP** (lo implementado) y **RAG** (explicado y comparado más abajo).

### 1.1 ¿Es un chat en el frontend? ¿Quién consume a quién?

Es la confusión más común. Aquí conviven **dos direcciones distintas**, y hay que separarlas:

- **`POST /assistant/chat` NO es un chat del frontend.** Es un **endpoint REST de tu propio backend**
  (el microservicio `assistant`, puerto 8095). Hoy **no hay una ventana de chat en la web Angular**;
  se prueba con `curl`/Postman (o, si se quiere, se puede añadir un componente de chat que lo llame).
- **"Claude" = el modelo LLM en la nube de Anthropic**, no la app Claude Desktop ni claude.ai.

**Dirección 1 — Tu backend CONSUME a Claude (esto es `/assistant/chat`):**
```
[ Cliente: curl / Postman / (futuro) chat en Angular ]
      │  POST /assistant/chat  {"message":"¿qué zapatillas hay?"}
      ▼
[ assistant :8095 (tu Spring Boot) ]
      │  Spring AI (ChatClient) --HTTPS + ANTHROPIC_API_KEY-->
      ▼
[ API de Anthropic / modelo Claude ]   (vive en la nube)
      │  el modelo decide: "necesito el catálogo" → pide ejecutar una tool
      ▼
[ assistant ejecuta ProductTools.listProducts() ] --RestClient--> [ product-service ]
      │  el resultado vuelve al modelo → Claude redacta la respuesta
      ▼
[ assistant ] devuelve JSON:  {"answer":"Tenemos adidas... $178.89, stock 5..."}
```
En esta dirección **tú consumes la API de Claude** (pagas tokens con tu API key). El "cliente" que
llama a `/assistant/chat` puede ser `curl`, Postman o —si se implementa— un componente de chat en la web.

**Dirección 2 — Un cliente externo CONSUME tu servicio, vía MCP (lo opuesto):**
```
[ Claude Desktop / Cursor / otro agente ]  --MCP (SSE)-->  [ assistant :8095 (servidor MCP) ]
                                                                    │
                                                                    ▼  descubre e invoca tus tools
                                                             ProductTools -> product-service
```
Aquí es **al revés**: tu `assistant` **expone** sus herramientas con el estándar MCP para que un
**cliente MCP externo** (por ejemplo Claude Desktop) las use.

| | Quién llama a quién | Qué es |
|---|---|---|
| **`/assistant/chat`** | tu backend → **API de Claude** (nube) | tu asistente propio; se prueba con `curl` (sin UI web todavía) |
| **Servidor MCP** | cliente MCP externo → tu backend | expone tus tools por el estándar MCP |

> **Estado actual:** existen la Dirección 1 (endpoint REST que consume Claude), la Dirección 2 (servidor
> MCP) y un **widget de chat en el frontend Angular** (burbuja flotante que hace `POST /assistant/chat`
> vía Kong; ver §4.1). El widget es el "Cliente" de la Dirección 1.

---

## 2. Conceptos

### 2.1 Tool Calling (function calling)
El LLM no sabe tu stock ni tus precios. Con *tool calling* le declaras **funciones** (“herramientas”)
que puede pedir ejecutar. El modelo decide, según la pregunta, llamar a `listProducts()` o
`getProduct(id)`; tu backend ejecuta la función (que llama al microservicio real), y el resultado
vuelve al modelo para que redacte la respuesta. Así la respuesta se basa en datos **en vivo**.

### 2.2 MCP — Model Context Protocol
**MCP** es un **protocolo abierto y estándar** (impulsado por Anthropic) para conectar modelos/agentes
con **herramientas y fuentes de datos externas**. En lugar de que cada app invente su forma de exponer
funciones, MCP define un contrato común: un **servidor MCP** publica *tools*, *resources* y *prompts*, y
cualquier **cliente MCP** (otro LLM, un IDE como Claude Desktop/Cursor, otro agente) los **descubre e
invoca** de forma uniforme.

Analogía: MCP es como un “USB‑C” para herramientas de IA — un mismo conector para todas.

En este proyecto, las mismas `@Tool` del catálogo se publican como **servidor MCP**, de modo que no
solo las usa nuestro `ChatClient`, sino que **cualquier cliente MCP externo** podría usarlas.

### 2.3 RAG — Retrieval-Augmented Generation
**RAG** = “generación aumentada por recuperación”. Sirve para que el modelo responda usando
**conocimiento propio no estructurado** (documentos, PDFs, políticas, FAQs) que no cabe en el prompt:
1. **Indexación (offline):** se parten los documentos en trozos, se generan **embeddings** (vectores)
   y se guardan en un **vector store**.
2. **Recuperación (online):** ante una pregunta, se buscan los trozos más **semánticamente similares**.
3. **Aumento + generación:** esos trozos se inyectan en el prompt y el modelo responde con ese contexto.

### 2.4 ¿Cuándo Tool Calling/MCP y cuándo RAG?
| | Tool Calling / MCP | RAG |
|---|---|---|
| Mejor para | Datos **estructurados y en vivo** (stock, precios, pedidos) | Conocimiento **no estructurado** (manuales, políticas, FAQs) |
| Frescura | Siempre actual (llama a la API en el momento) | Depende de cuándo se indexó |
| Cómo obtiene datos | Ejecuta funciones/consultas reales | Busca por similitud en vectores |
| En una tienda | Catálogo, stock, estado de un pedido | “¿Cuál es la política de devoluciones?” |

Para una tienda, el **stock y los precios** deben ser exactos y actuales → **Tool Calling/MCP es lo
correcto** (por eso es lo implementado). RAG encaja para preguntas sobre documentación/políticas.

---

## 3. Qué se implementó exactamente (y qué no)

**Implementado:**
- **Tool Calling** con `@Tool` que consultan `product-service` en vivo.
- **Servidor MCP** que publica esas herramientas para clientes MCP externos.
- Endpoint de chat `POST /assistant/chat` con `ChatClient` (modelo **Claude**).

**No implementado (documentado como extensión):** **RAG** (no hay vector store ni embeddings). Para
datos de catálogo, tool calling es más adecuado; RAG se añadiría para FAQs/políticas (ver §7). Se
documenta así de forma transparente.

### Archivos del módulo `assistant/`
```
assistant/
├── pom.xml                         # spring-ai-bom 2.0.1 + starter-model-anthropic + starter-mcp-server-webmvc
└── src/main/
    ├── java/.../assistant/
    │   ├── AssistantApplication.java
    │   ├── ProductTools.java        # @Tool listProducts()/getProduct(id) -> RestClient a product-service
    │   ├── AssistantConfig.java     # ToolCallbackProvider -> lo que publica el servidor MCP
    │   └── AssistantController.java  # POST /assistant/chat (ChatClient + tools)
    └── resources/application.yml     # api-key, modelo, mcp.server, URL de product-service
```

### Flujo (chat)
```
Usuario → POST /assistant/chat → ChatClient(Claude)
                                   │  (el modelo decide llamar una tool)
                                   ▼
                         ProductTools.listProducts()/getProduct(id)
                                   │  RestClient
                                   ▼
                         product-service (catálogo real)
                                   │
                                   ▼
             el resultado vuelve al modelo → respuesta en lenguaje natural
```

### Cómo se expone MCP
`spring-ai-starter-mcp-server-webmvc` levanta un **servidor MCP** (transporte SSE). Las herramientas que
publica salen del bean `ToolCallbackProvider` (en `AssistantConfig`), construido con
`MethodToolCallbackProvider.builder().toolObjects(productTools)`. Config en `application.yml`:
```yaml
spring:
  ai:
    mcp:
      server:
        name: online-store-assistant
        version: 1.0.0
```

### Configuración por variables de entorno (`.env`)

Todo se controla con 3 variables. En Docker, ponlas en un archivo **`.env`** en la **raíz del repo
`youtube-microservices-spring/`** (misma carpeta que los `docker-compose*.yml`); Compose lo lee solo.
Hay una plantilla en **`.env.example`** (cópiala a `.env`). El `.env` está en `.gitignore` (no se sube).

| Variable | Por defecto | Para qué |
|---|---|---|
| `LLM_ENABLED` | `false` | Activa el LLM. `false` → arranca **sin clave** y el chat responde "desactivado". `true` → usa Claude. |
| `LLM_MODEL` | `claude-haiku-4-5` | Modelo de Anthropic. **Haiku = el más económico** (recomendado). Alternativa: `claude-sonnet-4-5`. |
| `ANTHROPIC_API_KEY` | (vacío) | Tu clave de Anthropic. Solo necesaria si `LLM_ENABLED=true`. |

Ejemplo de `.env`:
```dotenv
LLM_ENABLED=true
LLM_MODEL=claude-haiku-4-5
ANTHROPIC_API_KEY=sk-ant-...
```

En local (sin Docker) exporta esas variables antes de `spring-boot:run`. Internamente, `LLM_ENABLED`
activa/desactiva la autoconfiguración del modelo (`spring.ai.model.chat = anthropic | none`) mediante un
`EnvironmentPostProcessor`, por eso puede arrancar sin clave cuando está en `false`.

---

## 4. Cómo probarlo

### Requisitos
1. **`ANTHROPIC_API_KEY`** (necesaria para arrancar el modelo).
2. **product-service** en marcha (las tools consultan su catálogo). Lo más fácil: todo el stack
   `make up`, o al menos product-service + su base.

### Arrancar el asistente
```bash
export ANTHROPIC_API_KEY=sk-ant-...
export CLIENTS_PRODUCT_URL=http://localhost:8000   # vía Kong (o http://localhost:8091 directo)
cd youtube-microservices-spring
./mvnw -pl assistant spring-boot:run
```

### Probar el chat (Tool Calling en acción)
```bash
curl -s -X POST http://localhost:8095/assistant/chat \
  -H 'Content-Type: application/json' \
  -d '{"message":"¿Qué zapatillas tienen y a qué precio? ¿Hay stock?"}'
```
El modelo llamará a `listProducts()`; responderá con nombres, precios y stock **reales** del catálogo.
Otros ejemplos: `"Dame el detalle del producto 1"`, `"¿Cuál es el más barato?"`.

### Verificar la fuente de verdad (sin IA)
La tool devuelve lo mismo que:
```bash
curl -s http://localhost:8000/products   # catálogo público (lo que consulta la herramienta)
```

### Probar el servidor MCP
El servidor MCP queda expuesto por SSE (por defecto en `/sse`, mensajes en `/mcp/message`):
```bash
curl -N http://localhost:8095/sse        # abre el stream SSE del servidor MCP
```
Para un uso real, conéctalo desde un **cliente MCP** (p. ej. Claude Desktop, Cursor o un cliente
`spring-ai-starter-mcp-client`) apuntando a `http://localhost:8095`; el cliente descubrirá las tools
`listProducts` y `getProduct` y podrá invocarlas.

### Comprobar que arranca sin clave
El módulo **compila y empaqueta** sin `ANTHROPIC_API_KEY` (`./mvnw -pl assistant package`), pero para
**ejecutar** el chat necesitas la clave (el modelo se inicializa al arrancar).

### 4.1 Widget de chat en el frontend (Angular)

El frontend (`youtube-frontend-angular`) incluye un **widget de chat flotante** (burbuja abajo a la
derecha, `ChatWidget`) que llama a `POST /assistant/chat` **a través de Kong** (`API_BASE`), muestra la
conversación y sugerencias, y degrada con un mensaje si el asistente no está disponible.

Para que el widget responda de verdad, hay que **levantar el servicio `assistant`** (necesita clave) y
Kong (que ya tiene la ruta `/assistant`):
```bash
export ANTHROPIC_API_KEY=sk-ant-...
cd youtube-microservices-spring
docker compose -f docker-compose.all.yml up -d                 # stack (incluye Kong con ruta /assistant)
docker compose -f docker-compose.all.yml -f docker-compose.assistant.yml up --build -d assistant

# Frontend
cd ../youtube-frontend-angular && nvm use 22 && npm start        # http://localhost:4200
```
Abre http://localhost:4200, pulsa la burbuja de chat y pregunta por el catálogo. Si el servicio
`assistant` no está corriendo, Kong responde 503 y el widget muestra "no disponible" (comportamiento
esperado). El chat es **anónimo** (no requiere login): el asistente consulta el catálogo público.

---

## 5. Decisiones de diseño

- Las tools devuelven un DTO reducido (`ProductInfo`: id, name, description, price, stock) — solo lo
  que el modelo necesita, evitando exponer datos internos.
- El `system prompt` instruye al modelo a responder en español y a **usar las herramientas** en lugar
  de inventar datos (mitiga alucinaciones).
- Un único `ToolCallbackProvider` sirve tanto al `ChatClient` como al servidor MCP (una sola fuente de
  herramientas).

---

## 6. Seguridad y coste (notas)

- La `ANTHROPIC_API_KEY` es un secreto: va por variable de entorno, nunca en el código/repositorio.
- Cada llamada al chat consume tokens del modelo (coste). Para demos, limita el uso.
- Si se publica el servidor MCP, controla quién puede invocarlo (auth/red), igual que cualquier API.

---

## 7. Cómo se añadiría RAG (extensión futura)

Para responder FAQs/políticas (no catálogo), se añadiría:
1. Dependencias: un `VectorStore` (p. ej. `spring-ai-starter-vector-store-pgvector` reutilizando
   PostgreSQL, o uno en memoria para demo) y un modelo de **embeddings**.
2. **Indexar** documentos (política de envíos/devoluciones, FAQ) en el vector store.
3. Añadir un **`QuestionAnswerAdvisor`** al `ChatClient`:
   ```java
   ChatClient.builder(model)
     .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
     .build();
   ```
   El advisor recupera los trozos relevantes y los inyecta en el prompt automáticamente.
Resultado: un asistente **híbrido** — Tool Calling/MCP para datos en vivo + RAG para conocimiento
documental. Es el patrón recomendado.

---

## 8. Resumen

- El asistente usa **Tool Calling** (con `@Tool`) para consultar el catálogo real y expone esas
  herramientas por **MCP** (protocolo estándar para conectar modelos con herramientas).
- **MCP** = conector estándar modelo↔herramientas; **RAG** = recuperar documentos por similitud e
  inyectarlos en el prompt. Son complementarios, no lo mismo.
- Aquí **RAG no se implementó** (para catálogo, tool calling es lo correcto); queda documentado cómo
  añadirlo para FAQs/políticas.
