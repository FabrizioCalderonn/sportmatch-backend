# Documentación técnica - SportMatch Backend

Este documento describe la arquitectura, componentes, configuración y uso del proyecto "canchitas" (backend). Está escrito en español y recoge la información técnica necesaria para desarrollar, desplegar y mantener la aplicación.

**Resumen rápido**
- **Tecnologías:** Java 21, Spring Boot 3.5, Spring Data JPA, Spring Security (JWT), Maven, PostgreSQL, Lombok.
- **Estructura principal:** el código fuente está en `Canchitas/src/main/java/org/ncapas/canchitas`.
- **Puerto por defecto:** `8080` (configurable en `application.yml` o con la variable `PORT`).

**Índice**
- Visión general
- Estructura del proyecto
- Configuración y variables de entorno
- Base de datos
- Seguridad (JWT)
- Endpoints principales (resumen por controlador)
- Entidades principales
- Seeds y datos iniciales
- Compilación, ejecución y Docker
- Pruebas y recomendaciones
- Contacto y siguientes pasos

**Visión general**

Este servicio expone una API REST para gestionar canchas, lugares, zonas y reservas. Está pensado como backend para una aplicación tipo "SportMatch" que permite listar canchas, realizar reservas y administrar inventario (solo para administradores).

**Estructura del proyecto**

- `Canchitas/src/main/java/org/ncapas/canchitas` - paquete raíz con la aplicación.
  - `Controllers` - controladores REST (`CanchaController`, `ReservaController`, `UsuariosController`, `LugarController`, `CatalogoController`, `LoginController`, `HealthController`).
  - `entities` - entidades JPA (p. ej. `Cancha`, `Reserva`, `Usuario`, `Rol`, `Lugar`, `Zona`, `MetodoPago`).
  - `Service` - servicios que contienen la lógica de negocio (usar `*Service` para buscar la lógica correspondiente).
  - `repositories` - interfaces Spring Data JPA para acceso a datos.
  - `config` - configuración (ej. `DatabaseConfig`).
  - `security` - utilidades y clases relacionadas con JWT y seguridad.
  - `DTOs` - objetos de transferencia (requests y responses) usados por los controladores.

**Configuración y variables de entorno**

Ficheros relevantes:
- `Canchitas/src/main/resources/application.yml` — contiene valores por defecto y placeholders para variables de entorno.

Variables y propiedades importantes:
- `DATABASE_URL` — URL completa de la base de datos (puede venir en formato tipo Railway o JDBC). `DatabaseConfig` trata de parsearla.
- `PGUSER` / `PGPASSWORD` — usuario y contraseña de la BD (alternativa si `DATABASE_URL` no contiene credenciales).
- `PORT` — puerto en el que corre el servidor (por defecto 8080).
- `SHOW_SQL`, `DDL_AUTO` — configuración JPA/Hibernate.

Ejemplo de variables (PowerShell):

```powershell
$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/canchitas"
$env:PGUSER = "postgres"
$env:PGPASSWORD = "admin"
$env:PORT = "8080"
```

**Base de datos**

- Motor: PostgreSQL (dependencia en `pom.xml`).
- El proyecto usa JPA/Hibernate con entidades mapeadas a tablas.
- `DatabaseConfig` intenta soportar tanto `DATABASE_URL` tipo Railway (user:pass@host) como JDBC. Si la variable no está presente, usa la configuración por defecto del `application.yml`.

Recomendaciones:
- Crear la BD antes de arrancar la aplicación, o usar `DDL_AUTO=update` para que Hibernate cree/actualice el esquema en entornos de desarrollo (cuidado en producción).

**Seguridad (JWT)**

- Hay un `LoginController` que ofrece `POST /api/auth/login` y devuelve un JWT (`AuthResponseDTO`).
- La seguridad se integra con `AuthenticationManager` y utilidades `JwtUtil` (dentro del paquete `security`).
- Los endpoints restringidos usan anotaciones como `@PreAuthorize("hasRole('ADMIN')")` o `@PreAuthorize("hasRole('CLIENTE')")`.

Notas:
- El token JWT se genera por email/correo y se devuelve junto con el rol y datos del usuario.

**Endpoints principales (resumen por controlador)**

1) `HealthController`
- `GET /` – Info básica del servicio (service, status, version).
- `GET /health` – Estado `UP`.

2) `LoginController` (`/api/auth`)
- `POST /api/auth/login` – Autenticar y recibir JWT. Body: `AuthRequestDTO` (correo, contrasena).
- `POST /api/auth/logout` – Cerrar sesión (necesita autenticación).

3) `UsuariosController` (`/api/usuarios`)
- `GET /api/usuarios` – Listar usuarios.
- `GET /api/usuarios/{id}` – Obtener usuario por id.
- `POST /api/usuarios` – Crear usuario (body: `UsuarioRequestDTO`).
- `DELETE /api/usuarios/{id}` – Eliminar usuario.

4) `CanchaController` (`/api/canchas`)
- `GET /api/canchas/tipos` – Listar tipos de cancha (público).
- `POST /api/canchas` – Crear cancha (ROLE_ADMIN).
- `GET /api/canchas` – Listar canchas.
- `GET /api/canchas/{id}` – Detalle de cancha.
- `PUT /api/canchas/{id}` – Actualizar cancha (ROLE_CLIENTE o ROLE_ADMIN según anotación).
- `DELETE /api/canchas/{id}` – Eliminar cancha (ROLE_ADMIN).
- `GET /api/canchas/{id}/jornadas?dia={dia}` – Listar jornadas por día (parámetro `dia`).
- `GET /api/canchas/{id}/reservas` – Reservas por cancha (ROLE_ADMIN).

5) `LugarController` (`/api/lugares`)
- `GET /api/lugares` – Listar lugares (requiere JWT).
- `GET /api/lugares/{id}` – Obtener lugar por id.
- `POST /api/lugares` – Crear lugar (ROLE_ADMIN).
- `DELETE /api/lugares/{id}` – Eliminar lugar (ROLE_ADMIN).
- `GET /api/lugares/zonas` – Listar zonas (combo público en `LugarController`).

6) `CatalogoController` (`/api`)
- `GET /api/zonas/{idZona}/lugares` – Lugares por zona.
- `GET /api/lugares/{idLugar}/canchas` – Canchas por lugar.
- `GET /api/zonas` – Listar zonas.
- `GET /api/metodos-pago` – Listar métodos de pago.

7) `ReservaController` (`/api/reservas`)
- `GET /api/reservas` – Listar reservas (ROLE_ADMIN), opcionalmente filtrar por `fechaReserva`.
- `GET /api/reservas/{id}` – Detalle reserva (autenticado).
- `POST /api/reservas` – Crear reserva (ROLE_CLIENTE).
- `DELETE /api/reservas/{id}` – Eliminar reserva (ROLE_CLIENTE o ROLE_ADMIN).
- `GET /api/reservas/usuario/{usuarioId}?estado=...` – Reservas por usuario y opcional filtro por estado.
- `GET /api/reservas/fechas-ocupadas?canchaId={id}` – Fechas ocupadas por cancha.
- `GET /api/reservas/horas-ocupadas?canchaId={id}&fecha={fecha}` – Horas ocupadas por cancha en una fecha.

**Entidades principales (resumen)**

- `Cancha` – id, nombre, imágenes (colección), número de cancha, `TipoCancha`, `Lugar`, lista de `Jornada`.
- `Reserva` – id, fechaReserva (Date), horaEntrada (LocalTime), horaSalida (LocalTime), precioTotal, fechaCreacion, estado (`EstadoReserva`), relaciones con `Usuario`, `Lugar`, `MetodoPago`, `Cancha`.
- `Usuario` – id, nombre, apellido, correo, contrasena, `Rol`.

Para ver todos los campos revisar los ficheros en `Canchitas/src/main/java/org/ncapas/canchitas/entities`.

**Seeds y datos iniciales**

El proyecto incluye clases tipo `DataSeeder` en el código que generan roles, tipos de cancha, zonas, etc. (p. ej. `DataseederRol.java`, `TipoCanchaSeeder.java`). Estas clases ayudan a poblar datos durante el arranque en entornos de desarrollo.

**Compilación y ejecución**

Requisitos locales:
- Java 21
- Maven (se puede usar el wrapper `mvnw` incluido)
- PostgreSQL (o una base compatible con los settings)

Comandos básicos (PowerShell):

```powershell
# Compilar
./mvnw.cmd clean package -DskipTests

# Ejecutar aplicación (desde carpeta Canchitas)
cd Canchitas; ./mvnw.cmd spring-boot:run

# O ejecutar el JAR generado
java -jar Canchitas/target/canchitas-0.0.1-SNAPSHOT.jar
```

Variables de entorno útiles (ejemplo):

```powershell
$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/canchitas"
$env:PGUSER = "postgres"
$env:PGPASSWORD = "admin"
$env:PORT = "8080"
```

**Docker**

- Hay un `Dockerfile` en la carpeta `Canchitas/`. Para construir y ejecutar imagen:

```powershell
# Desde la raíz del repo
cd Canchitas
docker build -t canchitas-backend .
docker run -e DATABASE_URL="jdbc:postgresql://host:5432/canchitas" -e PGUSER=postgres -e PGPASSWORD=admin -p 8080:8080 canchitas-backend
```

Nota: En despliegues en plataformas como Railway o Heroku, `DATABASE_URL` puede venir en un formato distinto; `DatabaseConfig` incluye heurística para parsear credenciales.

**Pruebas y recomendaciones**

- Ejecutar tests con `./mvnw.cmd test`.
- Para desarrollo local, habilitar `SHOW_SQL=true` en `application.yml` o por variable de entorno para ver consultas SQL.
- No usar `ddl-auto=update` en producción; preferir migraciones (Flyway/Liquibase).
