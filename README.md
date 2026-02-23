<h1 align="center">📚 Library Service - Sistema Híbrido de Gestión de Bibliotecas</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Spring_Boot-3.0-brightgreen.svg?style=for-the-badge&logo=springboot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/PostgreSQL-15-blue.svg?style=for-the-badge&logo=postgresql" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/MongoDB-6.0-green.svg?style=for-the-badge&logo=mongodb" alt="MongoDB">
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED.svg?style=for-the-badge&logo=docker" alt="Docker">
  <img src="https://img.shields.io/badge/Java-17-orange.svg?style=for-the-badge&logo=java" alt="Java">
</p>

## 📌 Descripción del Proyecto

**Library Service** es un proyecto enfocado en aplicar patrones de diseño sólidos a través de una arquitectura de persistencia políglota (híbrida). El sistema se encarga de administrar integralmente los datos y operativas habituales de una biblioteca, ofreciendo un control transaccional relacional y un sistema de auditoría desestructurado en tiempo real.

El proyecto satisface la gestión de **3 entidades principales** mapeadas bajo el patrón Repository con JPA, así como operaciones avanzadas analíticas y de auditoría mediante bases de datos NoSQL y *frameworks de agregación*.

---

## 🏛️ Arquitectura de Datos y Bases de Datos

El sistema se bifurca en dos potentes tecnologías para abarcar todos los casos de uso:

### 1. PostgreSQL (Persistencia Relacional)
Maneja el _Core SQL_ y mantiene las propiedades **ACID** para garantizar la integridad en:
- `libro`: id, titulo, isbn, categoria, publicado_en, stock
- `miembro`: id, nombre, email, telefono, fecha_alta
- `prestamo`: id, libro_id, miembro_id, fecha_inicio, fecha_vencimiento, fecha_devolucion, estado

> **Relaciones Clave:** Se modelan dos relaciones de uno a muchos (`1:N`) desde las tablas principales (`libro`, `miembro`) hacia la tabla pivote de control de inventario (`prestamo`).

### 2. MongoDB (Persistencia NoSQL)
Garantiza el desacople del modelo rígido y alta capacidad de escritura gracias a dos colecciones de documentos JSON para operaciones rápidas e históricas:
- `audit_logs`: Trazabilidad completa y automática (eventos de seguridad o creación).
- `book_comments`: Inserción masiva de opiniones sobre lectura.

#### Ejemplo de Documento (Audit Log):
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

### Comentario de libro:
```json
{
	"libroId": 2,
	"user": "ana",
	"texto": "Muy recomendado",
	"createdAt": "2026-02-22T18:52:00Z"
}
```

---

## 🚀 Arranque Rápido y Despliegue

La aplicación está preparada para su despliegue mediante contenedores.

### Requisitos Previos
- **Docker** y **Docker Compose**.
- JDK 17 o superior.
- Maven 3.x.

### Paso 1: Levantar Bases de Datos
En la carpeta raíz del proyecto, inicialice el clúster híbrido de base de datos ejecutando:
```bash
docker compose -f ./docker-compose.yml up -d
```
*(Esto levantará un contenedor de Postgres en el puerto `5432` y uno de MongoDB en el puerto extraído `27018` hacia el host).*

### Paso 2: Ejecutar el Servicio Spring Boot
Diríjase al módulo base para compilar e iniciar el servidor (puerto `8080`):
```bash
cd library-service
mvn spring-boot:run
o
& "C:\ruta_maven\bin\mvn.cmd" -f ".\library-service\pom.xml" spring-boot:run
```

---

## 📡 Endpoints de la API REST

### Operaciones Estándar (CRUD)
| Entidad | GET (Listar / Obtener) | POST (Crear) | PUT (Editar) | DELETE (Eliminar)|
|---------|------------------------|--------------|--------------|------------------|
| Libros | `/api/libros` | `/api/libros` | `/api/libros/{id}` | `/api/libros/{id}` |
| Miembros | `/api/miembros` | `/api/miembros` | `/api/miembros/{id}` | `/api/miembros/{id}` |
| Préstamos | `/api/prestamos` | `/api/prestamos` | `/api/prestamos/{id}` | `/api/prestamos/{id}` |

### Consultas Avanzadas y Analítica (SQL / NoSQL)

El proyecto incluye endpoints implementados en la lógica de negocio enfocados expresamente en el rendimiento y la agregación nativa en BD:

- **SQL:** Obtener los préstamos actuales donde la fecha de devolución está fuera del plazo estipulado.
  > `GET /api/prestamos/vencidos`
- **SQL:** Obtener el top de los 5 libros más prestados del sistema.
  > `GET /api/prestamos/top-libros?limit=5`
- **MongoDB:** Realizar comentarios flexibles en base a un modelo no estructurado.
  > `POST /api/comentarios?libroId=1`
- **MongoDB:** Implementación del *Aggregation Framework* para generar el TOP 5 de obras con mayor volumen de interacción ("Más Comentados").
  > `GET /api/comentarios/top-libros?limit=5`

---

## 💻 Ejemplos Básicos de Uso (cURL)

**1. Listar el inventario de libros disponibles con paginación optimizada:**
```bash
curl "http://localhost:8080/api/libros?page=0&size=5&sort=titulo,asc"
```

**2. Tramitar un nuevo préstamo emitiendo evento de auditoría en Mongo:**
```bash
curl -X POST http://localhost:8080/api/prestamos \
  -H 'Content-Type: application/json' \
  -H 'X-USER: alumno' \
  -d '{"libroId":1,"miembroId":1,"fechaVencimiento":"2026-03-01T12:00:00"}'
```

---

## 🛠️ Entornos Interactivos Integrados de Pruebas

Si desea inspeccionar las bases de datos en profundidad durante el transcurso de las operaciones:

**Consola SQL (PostgreSQL):**
```bash
docker exec -it grupo6_proyectofinal-main-postgres-1 psql -U postgres -d librarydb

o

curl http://localhost:8080/api/prestamos
```

**Consola NoSQL (MongoDB):**
```bash
docker exec -it grupo6_proyectofinal-main-mongo-1 mongosh librarydb

db.audit_logs.find().pretty()
```
*También es posible conectarse utilizando MongoDB Compass especificando la cadena de conexión nativa `mongodb://localhost:27018`.*

---

**Grupo 6: Johnny, Cerezo y David**