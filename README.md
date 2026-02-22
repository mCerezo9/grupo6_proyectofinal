# Proyecto: Acceso a Datos - Actividad

Este proyecto se centra en tres entidades: `Libro`, `Miembro` y `Prestamo`, implementadas en un servicio Spring Boot.

Servicios:
- Postgres: persistencia relacional para libros, miembros y prestamos.
- MongoDB: auditoria de operaciones en `audit_logs` y comentarios de libros en `book_comments`.

Modelo de datos (relacional):

Libro (1) ──< Prestamo >── (1) Miembro
Libro: id, titulo, isbn, categoria, publicadoEn, stock
Miembro: id, nombre, email, telefono, fechaAlta
Prestamo: id, libro_id, miembro_id, fechaInicio, fechaVencimiento, fechaDevolucion, estado

Ejemplos de documentos Mongo:
Audit log:

```json
{
	"timestamp": "2026-02-22T18:50:00Z",
	"type": "CREATE_PRESTAMO",
	"user": "alumno",
	"entityType": "Prestamo",
	"entityId": 12,
	"payload": {
		"libroId": 2,
		"miembroId": 1,
		"fechaVencimiento": "2026-03-01T12:00:00"
	}
}
```

Comentario de libro:
```json
{
	"libroId": 2,
	"user": "ana",
	"texto": "Muy recomendado",
	"createdAt": "2026-02-22T18:52:00Z"
}
```

Arranque rapido (PowerShell):
1) Levantar bases de datos:
```powershell
docker compose -f .\docker-compose.yml up -d
```

2) Ejecutar el servicio:
```powershell
& "C:\Users\ManuC\.maven\maven-3.9.12(3)\bin\mvn.cmd" -f ".\library-service\pom.xml" spring-boot:run
```
Endpoints principales:
- `GET /api/libros`, `POST /api/libros`, `PUT /api/libros/{id}`, `DELETE /api/libros/{id}`
- `GET /api/miembros`, `POST /api/miembros`, `PUT /api/miembros/{id}`, `DELETE /api/miembros/{id}`
- `GET /api/prestamos`, `GET /api/prestamos/{id}`, `POST /api/prestamos`, `PUT /api/prestamos/{id}`, `DELETE /api/prestamos/{id}`
- Consultas SQL avanzadas:
	- `GET /api/prestamos/vencidos`
	- `GET /api/prestamos/top-libros?limit=5`
- MongoDB (comentarios):
	- `POST /api/comentarios`
	- `GET /api/comentarios?libroId=1`
	- `GET /api/comentarios?user=ana`
	- `GET /api/comentarios?from=2026-02-01T00:00:00Z&to=2026-02-28T23:59:59Z`
	- `GET /api/comentarios/top-libros?limit=5`
---

# Library Service (Spring Boot)

Este módulo es un esqueleto de servicio Spring Boot para la práctica: gestiona `Libro`, `Miembro` y `Prestamo` con persistencia SQL (Postgres) y documentos de auditoría en MongoDB.
Requisitos mínimos: Docker y Maven.

Arrancar bases (en la raíz del repo):
```bash
docker-compose up -d
```

Construir y ejecutar la app:
```bash
cd library-service
mvn spring-boot:run
```

Endpoints útiles (ejemplos):

- Listar libros (paginado):
```bash
curl 'http://localhost:8080/api/libros?page=0&size=5&sort=titulo,asc'
```

- Crear préstamo (registra auditoría en Mongo):
```bash
curl -X POST http://localhost:8080/api/prestamos \
  -H 'Content-Type: application/json' \
  -H 'X-USER: alumno' \
  -d '{"libroId":1,"miembroId":1,"fechaVencimiento":"2026-03-01T12:00:00"}'
```

- Listar préstamos:
```bash
curl http://localhost:8080/api/prestamos
```

Comprobación en Mongo (desde host):
```bash
docker exec -it <mongo_container> mongosh
use librarydb
db.audit_logs.find().pretty()
```