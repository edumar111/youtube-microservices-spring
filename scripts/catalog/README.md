# Carga del catálogo (Excel → base de datos)

Genera y carga el catálogo de productos (593 ítems de TI/Computación) desde el Excel de Temu
hacia la base de datos de **product-service** (`productdb`).

## Camino elegido: SQL (por qué)

Para datos **estáticos y masivos** de catálogo, un **script SQL** es el mejor camino:
- Carga en **una sola transacción**, rápida y atómica.
- **No requiere autenticación** ni levantar la app (va directo a PostgreSQL).
- **Reproducible y revisable** (queda en el repo).

Alternativa **JSON + API REST** (`POST /products`): pasaría por las reglas de dominio/validación,
pero exige **login (JWT)**, **593 llamadas** y es más lento. Útil para importaciones incrementales
con validación de negocio. Por eso se genera también `catalog.json` (por si se usa ese camino).

## Archivos

Los artefactos que usan los alumnos viven en **`data/`** (no aquí):

| Archivo | Qué es |
|---|---|
| `scripts/catalog/generate_catalog_sql.py` | Generador: lee el `.xlsx` (stdlib, sin dependencias) y produce el SQL y el JSON en `data/`. |
| `data/catalog_import.sql` | **Script de carga** (categorías + 593 productos) para PostgreSQL/H2. |
| `data/catalog.json` | Mismos datos en JSON (camino alternativo vía API). |
| `data/temu_productos_TI_computacion_593.xlsx` | Fuente (hoja `Productos`). |

## Mapeo al esquema de `tbl_products`

| Columna Excel | Campo | Nota |
|---|---|---|
| Título | `name` | recortado a 255 |
| Título | `description` | recortado a 1000 |
| Precio USD | `price` | |
| — | `stock` | por defecto **100** (el Excel no trae stock) |
| — | `status` | `CREATED` |
| — | `create_at` | `CURRENT_DATE` |
| Categoría | `category_id` | se crea si no existe; se referencia por nombre |

> Extras del Excel (imagen, rating, precio lista, descuento, Goods ID) **no** caben en el esquema
> actual; se conservan en `catalog.json`. Ver "Paso 2 sugerido".

## Regenerar el SQL/JSON

El generador escribe por defecto en `data/`:
```bash
python3 scripts/catalog/generate_catalog_sql.py
# (opcional) fuente y salida explícitas:
# python3 scripts/catalog/generate_catalog_sql.py data/temu_productos_TI_computacion_593.xlsx data
```

## Cargar en la base de datos (PostgreSQL)

Con el stack levantado (`make up`), desde la raíz del repo:
```bash
PG=$(docker ps --format '{{.Names}}' | grep postgres | head -1)
docker exec -i "$PG" psql -U store -d productdb -v ON_ERROR_STOP=1 < data/catalog_import.sql
# verificar
docker exec -i "$PG" psql -U store -d productdb -tAc "SELECT count(*) FROM tbl_products;"
```
Validado con `ROLLBACK`: inserta 593 productos sin errores.

## ⚠️ Persistencia entre reinicios (importante)

`product-service` usa `spring.sql.init.mode=always`, así que su `data.sql` (semillas demo: 3 productos)
**se re-ejecuta y borra/reinserta** en cada arranque. Si cargas el catálogo por `psql` y luego
**reinicias** product-service, el `DELETE` de `data.sql` borrará lo importado.

**Paso 2 sugerido** (para que el catálogo sea permanente), elige uno:
1. **Convertir este catálogo en la semilla**: usar `catalog_import.sql` como `data.sql` del perfil de
   ejecución en Docker (dejando la semilla demo solo para tests/local H2).
2. **Desactivar la reinicialización en runtime**: perfil `docker` con `spring.sql.init.mode=never` y
   cargar el catálogo una vez (los tests siguen usando `always` con su semilla demo).
3. Cargar el catálogo **después** del arranque y no reiniciar el servicio.

> Nota: los tests (`ProductServiceIntegrationTest`) esperan las **3 semillas demo**, por eso la semilla
> demo debe permanecer para el perfil de tests; el catálogo real es para el runtime.

Para un catálogo más rico (imagen, rating, precio lista, descuento), habría que **extender el esquema y
el dominio** de product-service con esos campos — es una mejora aparte (los datos ya están en el JSON).
