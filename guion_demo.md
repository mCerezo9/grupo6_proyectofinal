# Comandos para la Demo (Copiar y Pegar)

### Paso 0: Preparativos (Antes de que empiece la presentación)
Asegurarse de tener esto ejecutado en PowerShell para tener el sistema vivo:

1. Levantar contenedores:
```bash
docker compose -f ./docker-compose.yml up -d
```
2. Levantar la aplicación web/servicio Spring Boot:
```bash
mvn spring-boot:run

o

& "C:\\Users\\ManuC\\.maven\\maven-3.9.12(3)\\bin\\mvn.cmd" -f ".\\library-service\\pom.xml" spring-boot:run

o

& "C:\\maven\\apache-maven-3.9.6\\bin\\mvn.cmd" -f ".\\library-service\\pom.xml" spring-boot:run
```

---

### 👉 DIAPOSITIVA 4: Comprobación de la Integración en MongoDB

Una vez que haces el préstamo en la interfaz Web, demuestra que se ha creado el historial JSON en Mongo:

**1. Entrar en la consola de MongoDB desde PowerShell:**
```bash
docker exec -it grupo6_proyectofinal-main-mongo-1 mongosh librarydb
```
**TAMBIÉN SE PUEDE DESDE MONGOSH**

**2. Ejecutar la búsqueda del último evento registrado:**
```javascript
db.audit_logs.find().sort({timestamp: -1}).limit(1).pretty()
```
*(Debería imprimir el JSON con la etiqueta "CREATE_PRESTAMO")*

---

### 👉 DIAPOSITIVA 5: Ejecución de las Consultas Avanzadas

Tened a mano los scripts de SQL y Mongo para pegarlos rápido.

#### 🐘 Consulta 1: SQL Avanzada con JOINs en Postgres 
**Libros vencidos**

**1. Entrar en la consola de PostgreSQL desde PowerShell:**
```bash
docker exec -it grupo6_proyectofinal-main-postgres-1 psql -U postgres -d librarydb
```

**2. Código a pegar para sacar los libros vencidos:**
```sql
SELECT m.nombre AS Miembro, l.titulo AS Libro, p.fecha_vencimiento AS Vencimiento 
FROM prestamos p 
JOIN miembros m ON p.miembro_id = m.id 
JOIN libros l ON p.libro_id = l.id 
WHERE p.fecha_devolucion IS NULL AND p.fecha_vencimiento < CURRENT_TIMESTAMP 
ORDER BY p.fecha_vencimiento ASC;
```

#### 🍃 Consulta 2: Mongo por Filtrado Clásico
Si sigues dentro de `mongosh` desde la diapositiva 4, solo pega esto. Si no, abre otra consola usando el comando Docker del principio.
```javascript
db.book_comments.find({ user: "yoni" }).pretty()
```

#### 🍃 Consulta 3: Mongo con Framework de Agregación
Pégalo en `mongosh`. Te dará el Top 5 de libros más comentados:
```javascript
db.book_comments.aggregate([
  { $group: { _id: "$libroId", totalComentarios: { $sum: 1 } } },
  { $sort: { totalComentarios: -1 } },
  { $limit: 5 }
])
```

*(Nota: Si decides hacerlo visualmente usando **MongoDB Compass**)*:
Conéctate a `mongodb://localhost:27018`. Ve a `book_comments`, pestaña **Aggregations** y añade estas tres etapas (`stages`):
1. **$group:** `{ _id: "$libroId", totalComentarios: { $sum: 1 } }`
2. **$sort:** `{ totalComentarios: -1 }`
3. **$limit:** `5`
