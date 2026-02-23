# Guion de Presentación: Library Service (Orientado a Rúbrica de Evaluación)

## Información General
- **Duración estimada:** 8 - 10 minutos.
- **Formato:** Presentación de arquitectura y demostración técnica.
- **Equipo:** David, Yoni, Cerezo.

---

## 🎬 PARTE 1: Introducción y Reglas de Negocio
**Orador:** David
**Diapositiva:** 1 (Título, Tema y Reglas de Negocio)
**Tiempo:** 1 minuto

> 💡 **TICK RÚBRICA (Calidad Técnica y Exposición):** Al nombrar Spring Boot, recalca que el código está ordenado por capas. Eso suma puntos en "Calidad Técnica (2)".

**Texto de Apoyo:**
> "Buenos días. Somos el Grupo 6 y os presentamos nuestro **Library Service**, un sistema para gestionar una biblioteca. Lo hemos desarrollado usando Spring Boot y nos hemos preocupado mucho de que el código esté limpio y muy bien ordenado en diferentes capas (Controladores, Servicios y Repositorios), controlando también todos los posibles errores.
> La regla principal de nuestra biblioteca es asegurarnos de que los libros prestados estén controlados al milímetro y no haya fallos. Para lograrlo, y al mismo tiempo poder guardar un historial súper detallado de todo lo que ocurre, hemos montado un sistema de base de datos doble: o sea, una arquitectura híbrida."

---

## 🎬 PARTE 2: El Modelo SQL y MongoDB
**Orador:** Yoni
**Diapositiva:** 2 (Modelo SQL) y 3 (¿Por qué Mongo?)
**Tiempo:** 2 - 2.5 minutos

> 💡 **TICK RÚBRICA (Funcionalidad SQL / CheckList):** Aquí justificamos "3 entidades JPA y 2 relaciones". Habla claro y seguro.

**Texto de Apoyo (Slide 2 - SQL):**
> "Para la parte de los datos estrictos, que no pueden fallar, usamos PostgreSQL en el bloque central del proyecto. Cumpliendo con lo que se pide en la lista de verificación, hemos creado **tres entidades JPA**: el *Libro*, el *Miembro* y el *Préstamo*.
> Entre ellas hemos construido **dos relaciones clave**: un libro puede prestarse muchas veces a lo largo del año (lo que sería una relación 1 a N), y un miembro también puede hacer muchos préstamos (otra 1 a N). Con esto, tenemos nuestro sistema CRUD totalmente operativo, sin que se nos descuadren los datos nunca."

> 💡 **TICK RÚBRICA (MongoDB + JSON / Integración SQL ↔ Mongo):** Se justifica el motivo del uso de MongoDB ("no porque sí"), que otorga puntos en "Justificación de Integración (2)".

**Texto de Apoyo (Slide 3 - MongoDB):**
> "El problema es que una base de datos de las de toda la vida se ahoga si le metes muchísima información de golpe que no tiene una forma fija. Por eso decidimos usar MongoDB. Aquí no metemos datos 'porque sí', sino documentos JSON que tienen todo el sentido del mundo. Tenemos dos colecciones: los *logs de auditoría* (que guardan cada movimiento del sistema tal cual pasa) y los *comentarios de los libros*. Guardar todo esto en Mongo hace que nuestra base principal (Postgres) no se quede atascada y vaya súper fluida."

---

## 🎬 PARTE 3: Demostración Práctica (En Vivo)
**Orador:** Cerezo
**Diapositiva:** 4 (Integración y CRUD) y 5 (Consultas Avanzadas)
**Tiempo:** 4 - 5 minutos

> 💡 **TICK RÚBRICA (Integración SQL ↔ Mongo - 2pts):** Aquí demostramos físicamente la integración "clara en flujos".

### Diapositiva 4: Integración SQL - MongoDB
> "Vamos a ver cómo funciona esto en directo. Hemos seguido los pasos de nuestro README para arrancar. Lo primero es ver cómo ambos mundos, el SQL y el de Mongo, se sincronizan solos. Voy a crear un préstamo como si fuera un usuario, desde la web."

*(Acción: Cerezo entra en la web y crea un prestamo con cualquier usuario y lo comprueba en `/api/prestamos`)*
> "El CRUD operativo transacciona en SQL correctamente. Ahora, si acudimos a nuestra instancia de MongoDB con un simple `find()`, observamos cómo el evento se ha sincronizado devolviendo un documento JSON estructurado en `audit_logs` con nuestra trazabilidad."

*(Acción: Cerezo muestra la consola de Mongo y ejecuta `db.audit_logs.find().sort({timestamp: -1}).limit(1).pretty()` para enseñar la integración).*

Output:
```json
{
  _id: ObjectId('699b8df2f8925254e416f398'),
  timestamp: 2026-02-22T23:14:58.008Z,
  type: 'CREATE_PRESTAMO',
  user: 'web',
  entityType: 'Prestamo',
  entityId: 3,
  payload: {
    libroId: 2,
    fechaVencimiento: '2026-02-28T05:19',
    miembroId: 2
  },
  _class: 'com.example.library.mongo.AuditLog'
}
```

### Diapositiva 5: Consultas Avanzadas
> "Para terminar la demo, vamos a lanzar un par de consultas a la base de datos."

> 💡 **TICK RÚBRICA (Funcionalidad SQL - 3pts):** Mostramos "2 consultas SQL avanzadas". Aparte del script, menciona que en el back end hay más lógicas avanzadas o nombra 2 endpoints.

**1. Consulta SQL (Postgres):**
*(Acción: Cerezo muestra la consola Postgres u otra CLI)*
> "Empezamos con PostgreSQL. Hemos preparado consultas avanzadas en SQL, y os voy a mostrar una que cruza tres tablas a la vez con un 'JOIN'. Nos sirve para descubrir, al vuelo, qué miembros de la biblioteca se están haciendo los remolones y no han devuelto sus libros."

**Comando Consola Postgres:**
```bash
docker exec -it grupo6_proyectofinal-main-postgres-1 psql -U postgres -d librarydb
```
**Código SQL**
```sql
SELECT m.nombre AS Miembro, l.titulo AS Libro, p.fecha_vencimiento AS Vencimiento 
FROM prestamos p 
JOIN miembros m ON p.miembro_id = m.id 
JOIN libros l ON p.libro_id = l.id 
WHERE p.fecha_devolucion IS NULL AND p.fecha_vencimiento < CURRENT_TIMESTAMP 
ORDER BY p.fecha_vencimiento ASC;
```
> *(Nota para Cerezo)*: "La segunda consulta SQL avanzada (`top-libros`) la tenemos programada en el código mediante JPA `@Query` con sentencias de agrupación y conteo."

> 💡 **TICK RÚBRICA (MongoDB + JSON - 2pts):** "2 consultas Mongo". Lanzamos una de filtro básica y una agregación potente.

**2. Consulta MongoDB (Agregación + Filtro):**
*(Acción: Cerezo muestra MongoDB Compass)*
> "Saltamos a MongoDB, donde vamos a mostrar nuestras dos consultas NoSQL. La primera es un filtrado clásico JSON para bucear por la base de datos buscando, por ejemplo, los comentarios que ha dejado un usuario de la aplicación en concreto."
*(Cerezo ejecuta Consulta Mongo 1 - El filtro)*:
```javascript
db.book_comments.find({ user: "yoni" }).pretty()
```
> "Y en segundo lugar, una **consulta de agregación agresiva** para procesar la estadística de los libros más populares en base a volumen de comentarios."
*(Cerezo ejecuta Consulta Mongo 2 - La Agregación)*:
```javascript
db.book_comments.aggregate([
  { $group: { _id: "$libroId", totalComentarios: { $sum: 1 } } },
  { $sort: { totalComentarios: -1 } },
  { $limit: 5 }
])
```
*(Opción B) En el constructor visual de MongoDB Compass:*
- **Etapa 1:** Seleccionar `$group` y pegar `{ _id: "$libroId", totalComentarios: { $sum: 1 } }`
- **Etapa 2:** Seleccionar `$sort` y pegar `{ totalComentarios: -1 }`
- **Etapa 3:** Seleccionar `$limit` y pegar `5`

---

## 🎬 PARTE 4: Cierre y Mejoras
**Orador:** David, Yoni & Cerezo
**Diapositiva:** 6 (Conclusiones)
**Tiempo:** 1 minuto

**David:** "Para concluir, lo que más nos ha costado pero que más nos ha enseñado ha sido mantener el proyecto limpio, ordenado, y saber combinar muy bien las dos bases de datos: usar Postgres cuando la información no puede tener fallos, y usar MongoDB para darle velocidad y desahogar el sistema."
**Cerezo:** "A nivel de infraestructura, trabajar directamente con Docker para el ecosistema entero nos ha quitado muchísimos dolores de cabeza instalando cosas a mano."
**Yoni:** "Personalmente, creo que este proyecto nos ha preparado para el mundo real, donde casi nunca existe un sistema con una sola base de datos de principio a fin. Como mejora se podría afianzar la conexión entre ambas bases de datos, por si ocurren caídas para que la información quede siempre perfecta. Si tenéis cualquier duda, somos todo oídos. ¡Muchas gracias!"
