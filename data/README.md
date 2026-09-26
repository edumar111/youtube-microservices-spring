# Datos del catálogo (para alumnos)

Aquí están los archivos **listos para cargar** el catálogo de productos (593 ítems de TI/Computación)
en la base de datos de `product-service`. **No necesitas el Excel**: usa el SQL o el JSON.

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

## ⚠️ Nota sobre reinicios
`product-service` reinicia su semilla demo (3 productos) al arrancar (`spring.sql.init.mode=always`),
lo que borraría lo importado si **reinicias** el servicio. Si vas a reiniciar, vuelve a ejecutar la
carga, o pide al instructor la configuración para hacerlo permanente (ver `scripts/catalog/README.md`).

## ¿Cómo se generaron estos archivos?
Con el generador `scripts/catalog/generate_catalog_sql.py` (lee el Excel y produce el SQL y el JSON).
Los alumnos normalmente **no** ejecutan el generador; solo usan `catalog_import.sql` / `catalog.json`.
