# TDD vs. Testcontainers — reporte

> Aclara si el proyecto usó TDD, qué son los tests con Testcontainers y en qué se diferencian.
> Contexto: `youtube-microservices-spring` (curso Spring Boot 4 / Java 25, Digital Lab Academy).

---

## 1. Respuesta corta

- **¿Se implementó TDD en este proyecto?** No en sentido estricto. **No** se escribieron los tests
  *antes* del código (que es la esencia de TDD). El código se implementó primero y los tests de
  integración se añadieron para **verificar** cada capa/episodio.
- **¿Qué se usó entonces?** Tests de integración (varios con **Testcontainers**) que arrancan
  dependencias reales (PostgreSQL, Kafka) y validan el comportamiento end-to-end de cada servicio.
- **¿Son lo mismo TDD y Testcontainers?** No. Son **ejes ortogonales**:
  - **TDD** = *metodología*: **cuándo y por qué** escribes las pruebas (primero el test, luego el código).
  - **Testcontainers** = *herramienta/librería*: **cómo** ejecutas pruebas contra servicios reales en Docker.
  - Se pueden combinar (hacer TDD *usando* Testcontainers), pero uno no implica al otro.

---

## 2. Qué es TDD (Test-Driven Development)

Metodología de desarrollo donde **la prueba se escribe antes que el código de producción**. Ciclo
**Red → Green → Refactor**:

1. **Red**: escribes un test que describe el comportamiento deseado. Falla (aún no hay código).
2. **Green**: escribes el mínimo código para que el test pase.
3. **Refactor**: mejoras el diseño manteniendo los tests en verde.

Se repite en ciclos muy cortos. Beneficios: diseño guiado por el uso, alta cobertura por construcción,
red de seguridad para refactorizar, documentación viva del comportamiento.

TDD es **independiente de la herramienta**: puedes hacer TDD con JUnit + mocks (tests unitarios),
con MockMvc (tests de web slice) o con Testcontainers (tests de integración).

---

## 3. Qué son los tests con Testcontainers

**Testcontainers** es una librería de test que levanta **contenedores Docker reales** durante la
ejecución de las pruebas (bases de datos, brokers, etc.) y los destruye al terminar. En lugar de
simular la dependencia (un mock, o una BD en memoria como H2), pruebas contra **la tecnología real**.

En este proyecto (Boot 4 → artefactos `testcontainers-<módulo>`):

- **PostgreSQL real** con `@Testcontainers` + `@ServiceConnection` (Spring Boot cablea el datasource
  al contenedor automáticamente). Ejemplos:
  - `product-service/.../ProductServiceIntegrationTest.java` — seeds, `updateStock`, stock insuficiente.
  - `customer-service/.../CustomerServiceIntegrationTest.java` — cliente sembrado, alta con estado.
  - `shopping-service/.../ShoppingServiceIntegrationTest.java` — enriquecido por `productId` (fix del
    bug), y escritura en la tabla **outbox** al crear la factura.
- **Kafka real** (KRaft) con `@DynamicPropertySource` apuntando al contenedor:
  - `product-service/.../ProductSagaKafkaTest.java` — publica `InvoiceCreated` en Kafka y verifica que
    el servicio **descuenta el stock** (participante de la saga del ep. 12).
- Tests que **no** usan Testcontainers (usan H2 o mocks, más rápidos):
  - `product-service/.../ProductSecurityWebTest.java` — MockMvc + `spring-security-test` (401 sin JWT, 200 con JWT).
  - `shopping-service/.../ResilienceFallbackTest.java` — H2 + mock del cliente HTTP para probar el fallback de Resilience4j.

Por qué Testcontainers y no solo H2: H2 no reproduce el dialecto ni el comportamiento real de
PostgreSQL (secuencias `IDENTITY`, tipos, constraints), y no existe un “H2 de Kafka”. Testcontainers
da **fidelidad**: “lo que pasa en el test es lo que pasará en producción”.

---

## 3.1 ¿Los tests con Testcontainers son unitarios o de integración?

**Son tests de INTEGRACIÓN, no unitarios.** Es una consecuencia directa de qué es Testcontainers:
levantar dependencias reales (PostgreSQL, Kafka) solo tiene sentido cuando pruebas la **integración**
entre tu código y esa infraestructura. Un test unitario, por definición, aísla una unidad y **no** usa
Docker ni servicios externos.

Recordatorio de la taxonomía:

| Tipo | Qué prueba | Dependencias | Velocidad | ¿Testcontainers? |
|---|---|---|---|---|
| **Unitario** | Una clase/método aislado (p. ej. lógica de dominio) | Mocks/stubs; sin Spring | Muy rápida (ms) | No |
| **De slice** | Una capa con contexto parcial (p. ej. `@WebMvcTest`, MockMvc) | Parte del contexto; mocks del resto | Rápida | No (normalmente) |
| **De integración** | Varias capas juntas contra dependencias **reales** | Contenedores reales (BD, broker) | Más lenta (s) | **Sí** |

### Inventario real de este repo (12 métodos `@Test` en 6 clases)

| Clase de test | Métodos | Tipo | Infraestructura |
|---|---:|---|---|
| `ProductServiceIntegrationTest` | 3 | Integración | **Testcontainers** PostgreSQL |
| `CustomerServiceIntegrationTest` | 2 | Integración | **Testcontainers** PostgreSQL |
| `ShoppingServiceIntegrationTest` | 2 | Integración | **Testcontainers** PostgreSQL (+ mocks de los puertos de cliente) |
| `ProductSagaKafkaTest` | 1 | Integración | **Testcontainers** PostgreSQL **+ Kafka** |
| `ProductSecurityWebTest` | 3 | Integración web (MockMvc + `spring-security-test`) | H2 (sin contenedores) |
| `ResilienceFallbackTest` | 1 | Integración | H2 + mock del cliente HTTP |

Conclusiones:
- **Los 8 tests que usan Testcontainers son de integración** (4 clases; una además levanta Kafka).
- Las otras 2 clases también cargan el contexto de Spring (`@SpringBootTest`): son de
  integración/“web slice”, pero con **H2 y mocks** en lugar de contenedores.
- **No hay tests unitarios puros** en el repo todavía (ninguna prueba de una clase aislada sin Spring).
  La arquitectura hexagonal los facilitaría (los puertos como `ProductRepositoryPort` son interfaces
  fáciles de mockear) — es una mejora natural para complementar los de integración.

> Regla práctica: **Testcontainers ⇒ integración**. Para tests unitarios no se usa Testcontainers
> (se usan mocks); para verificar el comportamiento contra Postgres/Kafka reales, sí.

---

## 4. La diferencia (lado a lado)

| | **TDD** | **Testcontainers** |
|---|---|---|
| Qué es | Metodología / disciplina de trabajo | Librería / herramienta de test |
| Responde a | *¿Cuándo escribo el test?* (antes del código) | *¿Contra qué ejecuto el test?* (dependencias reales en Docker) |
| Eje | Proceso | Infraestructura de pruebas |
| Alternativas | Test-after, BDD, "no tests" | Mocks, bases en memoria (H2), entornos compartidos |
| ¿Requiere Docker? | No | Sí |
| ¿Se pueden combinar? | — | Sí: se puede hacer TDD usando Testcontainers |
| En este repo | No (test-after / test junto al código) | Sí (PostgreSQL y Kafka reales) |

**Analogía:** TDD es *cuándo cocinas la prueba* (antes de servir el plato); Testcontainers es *con qué
ingredientes reales la cocinas* (Postgres/Kafka de verdad, no de plástico). Son preguntas distintas.

---

## 5. Qué se hizo realmente en este proyecto

Enfoque **test-after / test por episodio** (no TDD):

1. Implementar la funcionalidad del episodio (dominio hexagonal, adaptadores, config).
2. Escribir tests que la verifican, muchos con Testcontainers (Postgres/Kafka reales).
3. Ejecutar `./mvnw verify`; corregir hasta dejar todo en verde.
4. Commit del episodio.

Resultado: **15 tests** en verde cubriendo persistencia real, seguridad, resiliencia y la saga con
Kafka. Es una base de pruebas sólida, aunque el **flujo de trabajo no fue TDD**.

---

## 6. Cómo se vería hacer TDD (con Testcontainers) en este proyecto

Ejemplo con `updateStock` (regla: no permitir stock negativo):

1. **Red** — escribir primero el test (falla porque el método/regla no existe):
   ```java
   @Test
   void updateStockFailsWhenInsufficient() {
       assertThatThrownBy(() -> productUseCase.updateStock(2L, 9999.0))
           .isInstanceOf(InsufficientStockException.class);
   }
   ```
2. **Green** — implementar lo mínimo en `ProductService.updateStock` para que pase (validar y lanzar
   `InsufficientStockException`).
3. **Refactor** — limpiar el diseño (extraer la regla, nombres, etc.) manteniendo el test verde.

El test podría usar Testcontainers (PostgreSQL real) o, para el ciclo rápido de TDD, un test unitario
del dominio con un puerto de salida mockeado (el dominio hexagonal lo facilita: `ProductRepositoryPort`
es una interfaz fácil de simular). Una estrategia común: **TDD con unit tests rápidos** en el dominio
+ **tests de integración con Testcontainers** para validar los adaptadores reales.

---

## 7. Conclusión

- El proyecto **no usó TDD**, pero sí una **buena disciplina de pruebas** con tests de integración de
  alta fidelidad (Testcontainers) por episodio.
- **TDD y Testcontainers no compiten**: son cosas distintas y complementarias. Si se quisiera adoptar
  TDD en la temporada, encajaría de forma natural gracias a la arquitectura hexagonal (puertos fáciles
  de testear) y a Testcontainers ya configurado para los tests de integración.
