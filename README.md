# ddd_crud_springboot

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

Notas

- Se recomienda usar Java 21 (OpenJDK / Temurin).
- El proyecto incluye un workflow de GitHub Actions que ejecuta `mvn clean test` en cada push y PR.

Contribuir

1. Haz fork del repo y crea una rama para tu feature/bugfix.
2. Abre un Pull Request hacia `master`.

Licencia

Este repositorio incluye un archivo `LICENSE` (MIT). Si necesitas otra licencia, indícalo.

