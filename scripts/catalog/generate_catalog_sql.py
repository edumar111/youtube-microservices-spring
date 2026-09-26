#!/usr/bin/env python3
"""
Genera el script de carga del catálogo (SQL) a partir del Excel de Temu.

Lee la hoja 'Productos' de un .xlsx (sin dependencias externas: parseo con la stdlib) y produce:
  - catalog_import.sql : INSERTs para tbl_categories + tbl_products (PostgreSQL/H2), 1 transacción.
  - catalog.json       : mismos datos en JSON (camino alternativo vía API REST).

Mapeo al esquema de product-service:
  name        <- Título (recortado a 255)
  description <- Título (recortado a 1000)
  price       <- Precio USD
  stock       <- STOCK_DEFAULT (el Excel no trae stock)
  status      <- 'CREATED'
  create_at   <- CURRENT_DATE
  category    <- Categoría (se crea si no existe; se referencia por nombre)

Uso:
  python3 generate_catalog_sql.py <ruta.xlsx> [salida_dir]
"""
import json
import re
import sys
import zipfile
import xml.etree.ElementTree as ET
from pathlib import Path

NS = "{http://schemas.openxmlformats.org/spreadsheetml/2006/main}"
STOCK_DEFAULT = 100
SHEET_NAME = "Productos"


def read_sheet(xlsx_path, sheet_name):
    z = zipfile.ZipFile(xlsx_path)
    shared = []
    if "xl/sharedStrings.xml" in z.namelist():
        root = ET.fromstring(z.read("xl/sharedStrings.xml"))
        for si in root.findall(f"{NS}si"):
            shared.append("".join(t.text or "" for t in si.iter(f"{NS}t")))
    # map sheet name -> file
    wb = ET.fromstring(z.read("xl/workbook.xml"))
    rels = ET.fromstring(z.read("xl/_rels/workbook.xml.rels"))
    rid_to_target = {r.get("Id"): r.get("Target") for r in rels}
    RNS = "{http://schemas.openxmlformats.org/officeDocument/2006/relationships}"
    target = None
    for s in wb.iter(f"{NS}sheet"):
        if s.get("name") == sheet_name:
            rid = s.get(f"{RNS}id")
            target = "xl/" + rid_to_target[rid].lstrip("/")
    if target is None:
        raise SystemExit(f"No se encontró la hoja '{sheet_name}'")

    def colnum(ref):
        letters = re.match(r"([A-Z]+)", ref).group(1)
        n = 0
        for ch in letters:
            n = n * 26 + (ord(ch) - 64)
        return n - 1

    root = ET.fromstring(z.read(target))
    rows = []
    for row in root.iter(f"{NS}row"):
        cells = {}
        for c in row.findall(f"{NS}c"):
            ref, t = c.get("r"), c.get("t")
            v = c.find(f"{NS}v")
            if t == "s" and v is not None:
                val = shared[int(v.text)]
            elif v is not None:
                val = v.text
            else:
                inl = c.find(f"{NS}is")
                val = "".join(x.text or "" for x in inl.iter()) if inl is not None else None
            cells[colnum(ref)] = val
        if cells:
            mx = max(cells)
            rows.append([cells.get(i) for i in range(mx + 1)])
    return rows


def clean(text):
    if text is None:
        return ""
    # normaliza espacios y quita rellenos de asteriscos del scraping
    s = re.sub(r"\s+", " ", str(text)).strip()
    s = re.sub(r"\s*\*\s*", " ", s)
    return re.sub(r"\s+", " ", s).strip()


def to_price(v):
    try:
        return round(float(str(v).replace(",", "")), 2)
    except (TypeError, ValueError):
        return None


def sql_str(s):
    return "'" + s.replace("'", "''") + "'"


def main():
    # Por defecto, fuente y salida en la carpeta data/ del repo (independiente del cwd).
    data_dir = Path(__file__).resolve().parents[2] / "data"
    xlsx = sys.argv[1] if len(sys.argv) > 1 else str(data_dir / "temu_productos_TI_computacion_593.xlsx")
    out = Path(sys.argv[2]) if len(sys.argv) > 2 else data_dir
    rows = read_sheet(xlsx, SHEET_NAME)
    header = [clean(h) for h in rows[0]]
    idx = {name: i for i, name in enumerate(header)}
    col = lambda r, name: r[idx[name]] if idx.get(name) is not None and idx[name] < len(r) else None

    products, categories, skipped = [], set(), 0
    for r in rows[1:]:
        title = clean(col(r, "Título"))
        price = to_price(col(r, "Precio USD"))
        category = clean(col(r, "Categoría")) or "General"
        if not title or price is None:
            skipped += 1
            continue
        categories.add(category)
        products.append({
            "name": title[:255],
            "description": title[:1000],
            "price": price,
            "stock": STOCK_DEFAULT,
            "status": "CREATED",
            "category": category,
            # extras (no van al esquema actual, pero se conservan en el JSON)
            "listPriceUsd": to_price(col(r, "Precio lista USD")),
            "rating": col(r, "Rating"),
            "image": clean(col(r, "Imagen")) or None,
            "goodsId": clean(col(r, "Goods ID")) or None,
        })

    # ids explícitos y estables de categoría (orden alfabético)
    cat_id = {name: i + 1 for i, name in enumerate(sorted(categories))}
    restart_at = 100000  # las secuencias arrancan muy por encima de las semillas

    # --- SQL idempotente (DELETE + inserts + RESTART): sirve como carga manual y como semilla ---
    lines = [
        "-- Catálogo generado desde el Excel de Temu. Idempotente: puede ejecutarse varias veces.",
        "-- Se usa como carga manual (psql) y como semilla del runtime en Docker (data-catalog.sql).",
        f"-- Productos: {len(products)} · Categorías: {len(categories)} · Filas omitidas: {skipped}",
        "DELETE FROM tbl_products;",
        "DELETE FROM tbl_categories;",
    ]
    for name in sorted(categories):
        lines.append(f"INSERT INTO tbl_categories (id, name) VALUES ({cat_id[name]}, {sql_str(name)});")
    lines.append("")
    lines.append("INSERT INTO tbl_products (id, name, description, stock, price, status, create_at, image_url, category_id) VALUES")
    tuples = []
    for i, p in enumerate(products, start=1):
        img = sql_str(p['image'][:1000]) if p.get('image') else "NULL"
        tuples.append(
            f"  ({i}, {sql_str(p['name'])}, {sql_str(p['description'])}, {p['stock']}, {p['price']}, "
            f"'CREATED', CURRENT_DATE, {img}, {cat_id[p['category']]})"
        )
    lines.append(",\n".join(tuples) + ";")
    lines.append("")
    lines.append(f"ALTER TABLE tbl_categories ALTER COLUMN id RESTART WITH {restart_at};")
    lines.append(f"ALTER TABLE tbl_products ALTER COLUMN id RESTART WITH {restart_at};")
    sql = "\n".join(lines) + "\n"

    (out / "catalog_import.sql").write_text(sql, encoding="utf-8")
    (out / "catalog.json").write_text(json.dumps(products, ensure_ascii=False, indent=2), encoding="utf-8")

    # También como semilla en el classpath de product-service (perfil docker).
    seed = Path(__file__).resolve().parents[2] / "product-service" / "src" / "main" / "resources" / "data-catalog.sql"
    seed.write_text(sql, encoding="utf-8")

    print(f"OK · productos={len(products)} categorías={sorted(categories)} omitidas={skipped}")
    print(f"  -> {out/'catalog_import.sql'}")
    print(f"  -> {out/'catalog.json'}")
    print(f"  -> {seed}")


if __name__ == "__main__":
    main()
