# Datos del catálogo (para alumnos)

Aquí están los archivos del catálogo de productos (593 ítems de TI/Computación). **No necesitas el Excel.**

> ✅ **Con `make up` el catálogo se carga AUTOMÁTICAMENTE y de forma permanente.** product-service
> arranca con el perfil `docker` y usa `data-catalog.sql` como semilla (sobrevive reinicios). Solo usa
> los comandos de abajo si quieres cargarlo **manualmente** en otra BD o recargarlo.

| Archivo | Para qué |
|---|---|
| **`catalog_import.sql`** | Script de carga directo a PostgreSQL (recomendado). |
| **`catalog.json`** | Mismos datos en JSON (por si cargas vía API o para inspeccionar). |
| `temu_productos_TI_computacion_593.xlsx` | Fuente original (no la necesitas para cargar). |

## Cargar el catálogo (opción recomendada: SQL)

Con el stack levantado (`make up`), desde la raíz del repo:

```bash
PG=$(docker ps --format '{{.Names}}' | grep postgres | head -1)
docker exec -i "$PG" psql -U store -d productdb -v ON_ERROR_STOP=1 < data/catalog_import.sql

# verificar cuántos productos quedaron
docker exec -i "$PG" psql -U store -d productdb -tAc "SELECT count(*) FROM tbl_products;"
```

Luego abre la tienda (http://localhost:4200) y verás el catálogo cargado.

> El script crea las categorías si no existen y agrega los 593 productos (una sola transacción).

## Permanencia
En Docker (`make up`), product-service usa el perfil `postgres,docker` y carga `data-catalog.sql`
(este mismo catálogo) como semilla **idempotente** en cada arranque → el catálogo queda **permanente**
y se re-crea igual tras cualquier reinicio (sin duplicados). La semilla demo de 3 productos solo se usa
en los tests y en el perfil `local` (H2).

## ¿Cómo se generaron estos archivos?
Con el generador `scripts/catalog/generate_catalog_sql.py` (lee el Excel y produce el SQL y el JSON).
Los alumnos normalmente **no** ejecutan el generador; solo usan `catalog_import.sql` / `catalog.json`.
