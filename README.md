# ddd_crud_springboot

[![CI](https://github.com/david92f/ddd_crud_springboot/actions/workflows/maven.yml/badge.svg)](https://github.com/david92f/ddd_crud_springboot/actions)
[![Package](https://github.com/david92f/ddd_crud_springboot/actions/workflows/package.yml/badge.svg)](https://github.com/david92f/ddd_crud_springboot/actions)

Proyecto de ejemplo DDD (Domain-Driven Design) en Java con Spring Boot.

Resumen

- Lenguaje: Java 21
- Build: Maven
- Objetivo: ejemplo CRUD y modelado por agregados/servicios de dominio

Cómo ejecutar localmente

1. Compilar y ejecutar tests:

```bash
mvn -B clean test
```

2. Compilar y ejecutar la aplicación (si la aplicación tiene un main):

```bash
mvn -B package
java -jar target/ddd-ejemplo-0.0.1-SNAPSHOT.jar
```

Hooks (opcional)

- Recomendación: usar `pre-commit` para formateo y `mvn test` rápido.
- Ejemplo con Husky (para proyectos JS) o githooks manual:

Create a file `.git/hooks/pre-commit` with:

```bash
#!/bin/sh
mvn -q -DskipTests=false test || { echo "Tests failed"; exit 1; }
```

Hazlo ejecutable:

```bash
chmod +x .git/hooks/pre-commit
```

API — Endpoints (detallado)

Base path: `/api/pedidos`

1) Crear pedido
- POST /api/pedidos
- Content-Type: application/json
- Body (ejemplo):

```json
{
  "idCliente": "cliente-123",
  "direccion": {
    "calle": "Calle Falsa 123",
    "ciudad": "Ciudad",
    "provincia": "Provincia",
    "codigoPostal": "28000",
    "pais": "ES"
  },
  "lineas": [
    { "idProducto": "producto-1", "cantidad": 2, "precioUnitario": 10.50 },
    { "idProducto": "producto-2", "cantidad": 1, "precioUnitario": 5.00 }
  ],
  "moneda": "EUR"
}
```

- Response 201 Created (ejemplo):

```json
{
  "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "estado": "CREADO",
  "total": 26.00,
  "moneda": "EUR"
}
```

2) Agregar línea a pedido
- POST /api/pedidos/{idPedido}/lineas
- Body (ejemplo):

```json
{ "idProducto": "producto-3", "cantidad": 1, "precioUnitario": 12.00 }
```

3) Actualizar dirección
- PUT /api/pedidos/{idPedido}/direccion
- Body (ejemplo):

```json
{ "calle": "Calle Nueva 5", "ciudad": "Ciudad", "codigoPostal": "28001" }
```

4) Obtener pedido por ID
- GET /api/pedidos/{idPedido}
- Response 200 (ejemplo):

```json
{
  "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "idCliente": "cliente-123",
  "direccion": { "calle": "Calle Nueva 5", "ciudad": "Ciudad", "codigoPostal": "28001" },
  "lineas": [ { "idProducto": "producto-1", "cantidad": 2, "precioUnitario": 10.50 } ],
  "total": 21.00,
  "estado": "CONFIRMADO"
}
```

Ejemplos curl

Crear pedido:

```bash
curl -X POST http://localhost:8080/api/pedidos \
  -H "Content-Type: application/json" \
  -d '@crearPedido.json'
```

Agregar línea:

```bash
curl -X POST http://localhost:8080/api/pedidos/{idPedido}/lineas \
  -H "Content-Type: application/json" \
  -d '{ "idProducto":"producto-2", "cantidad":1, "precioUnitario": 5.00 }'
```

Contribuir

1. Haz fork del repo y crea una rama para tu feature/bugfix.
2. Abre un Pull Request hacia `master`.

Licencia

Este repositorio incluye un archivo `LICENSE` (MIT). Si necesitas otra licencia, indícalo.
