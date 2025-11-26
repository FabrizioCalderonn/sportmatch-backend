<<<<<<< HEAD
# SportMatch Backend - Documentación Completa

## Tabla de Contenidos

1. [Descripción General](#descripción-general)
2. [Arquitectura y Tecnologías](#arquitectura-y-tecnologías)
3. [Diagramas UML](#diagramas-uml)
4. [Documentación de Procesos](#documentación-de-procesos)
5. [Documentación de Dominio](#documentación-de-dominio)
6. [Configuración y Ejecución](#configuración-y-ejecución)
7. [Endpoints API](#endpoints-api)
8. [Modelo de Datos](#modelo-de-datos)
9. [Reglas de Negocio](#reglas-de-negocio)

---

## Descripción General

**SportMatch** es una plataforma de gestión y reserva de canchas deportivas. El backend es responsable de manejar toda la lógica de negocio, autenticación, gestión de reservas, usuarios y disponibilidad de canchas.

**Características principales:**
-  Autenticación y autorización de usuarios (JWT)
-  Gestión de usuarios con roles diferenciados
-  Sistema completo de reservas de canchas
-  Catálogo de canchas y zonas geográficas
-  Gestión de jornadas y horarios
-  Métodos de pago
-  Notificaciones programadas

---

##  Arquitectura y Tecnologías

### Stack Tecnológico

| Componente | Versión | Descripción |
|-----------|---------|------------|
| **Framework** | Spring Boot 3.5.0 | Framework principal para desarrollo backend |
| **Java** | 21 | Lenguaje de programación |
| **Base de Datos** | PostgreSQL | Base de datos relacional |
| **ORM** | JPA/Hibernate | Mapeo objeto-relacional |
| **Seguridad** | Spring Security + JWT | Autenticación y autorización |
| **Validación** | Spring Validation | Validación de datos |
| **Build Tool** | Maven | Gestor de dependencias |
| **Contenedorización** | Docker | Despliegue en contenedores |

### Dependencias Principales

```xml
<!-- Spring Boot Starters -->
- spring-boot-starter (Core)
- spring-boot-starter-data-jpa (ORM)
- spring-boot-starter-security (Seguridad)
- spring-boot-starter-web (REST APIs)
- spring-boot-starter-validation (Validación)
- spring-boot-starter-jdbc (Conexiones BD)

<!-- Base de Datos -->
- postgresql (Driver PostgreSQL)

<!-- Autenticación -->
- jjwt (JWT - JSON Web Tokens) v0.11.5
- jjwt-api
- jjwt-impl
- jjwt-jackson

<!-- Utilidades -->
- lombok (Reducción de código boilerplate)
```

### Patrón Arquitectónico

La aplicación sigue una arquitectura de **N-Capas** (Layered Architecture):

```
┌─────────────────────────────────────┐
│        Controllers (REST API)        │
│  (Mapeo de rutas y validación)       │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│        Service Layer (Lógica)        │
│  (Reglas de negocio, procesamiento)  │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│    Repository Layer (Acceso Datos)   │
│  (Consultas a BD, persistencia)      │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│      Database Layer (PostgreSQL)     │
│  (Almacenamiento de datos)           │
└─────────────────────────────────────┘
```

---

##  Diagramas UML

### 1. Diagrama de Casos de Uso - Actores y Sus Interacciones

```
┌─────────────────────────────────────────────────────────────┐
│                      SPORTMATCH BACKEND                      │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────┐                      ┌─────────────────┐  │
│  │   USUARIO    │                      │  ADMINISTRADOR  │  │
│  │   (Cliente)  │                      │   (Gestor)      │  │
│  └──────┬───────┘                      └────────┬────────┘  │
│         │                                       │             │
│         │ ┌─────────────────────────────────────┤             │
│         │ │                                     │             │
│      ┌──▼─────────────────────────────────────▼──┐           │
│      │                                            │           │
│      │   • Registrarse/Autenticarse              │           │
│      │   • Ver catálogo de canchas               │           │
│      │   • Buscar disponibilidad                 │           │
│      │   • Realizar reservas                     │           │
│      │   • Cancelar reservas                     │           │
│      │   • Ver historial de reservas             │           │
│      │   • Gestionar métodos de pago             │           │
│      │   • Consultar estado de reservas          │           │
│      │                                            │           │
│      │   (Admin adicional)                       │           │
│      │   • Gestionar canchas                     │           │
│      │   • Gestionar usuarios                    │           │
│      │   • Configurar jornadas y horarios        │           │
│      │   • Consultar reportes                    │           │
│      │                                            │           │
│      └────────────────────────────────────────────┘           │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

### 2. Diagrama de Secuencia - Proceso de Reserva

```
USUARIO      CONTROLLER      SERVICE        REPOSITORY      DATABASE
   │             │              │              │              │
   │─ Solicita ──→│              │              │              │
   │  Reserva     │              │              │              │
   │              │─ Valida ────→│              │              │
   │              │  Datos       │              │              │
   │              │              │─ Verifica ──→│              │
   │              │              │  Disponibi.  │─ Consulta ──→│
   │              │              │              │  Disponibilidad
   │              │              │              │←─ Resultado ─│
   │              │              │←─ OK ────────│              │
   │              │              │              │              │
   │              │              │─ Calcula ───→│              │
   │              │              │  Precio      │              │
   │              │              │              │              │
   │              │              │─ Guarda ─────→─────────────→│
   │              │              │  Reserva     │   INSERT     │
   │              │              │              │←─ Guardado ──│
   │              │←─ Reserva ───│              │              │
   │←─ Confirmación───────────────→              │              │
   │              │              │              │              │
```

### 3. Diagrama de Actividad - Flujo de Reserva

```
                         INICIO
                            │
                            ▼
                    ¿Usuario autenticado?
                       /          \
                     SÍ            NO
                    /                \
            Buscar Canchas      Redirigir a Login
                │                     │
                ▼                     ▼
        Aplicar Filtros         Autenticarse
                │                     │
                ▼                     │
        Seleccionar Fecha             │
                │                     │
                ▼                     │
        ¿Cancha Disponible?           │
             /      \                 │
           SÍ        NO               │
          /            \              │
    Seleccionar     Mostrar Mensaje  │
    Horario         Indisponible    │
         │                \           │
         │                 └──────────┤
         │                            │
         ▼                            ▼
    Seleccionar Método                │
    de Pago                           │
         │                            │
         ▼                            │
    Revisar Detalles                  │
         │                            │
         ▼                            │
    Confirmar Reserva                 │
         │                            │
         ▼                            │
    ¿Pago Válido?                     │
       /      \                       │
     SÍ        NO                     │
    /            \                    │
Guardar Reserva  Mostrar Error       │
   │              Pago               │
   ▼              │                   │
Generar           ▼                   │
Confirmación   Reintentar             │
   │              │                   │
   └──────────────┤                   │
                  └───────────────────┤
                                      ▼
                                     FIN
```

### 4. Diagrama de Clases - Estructura del Dominio

```
┌─────────────────────────────────┐
│          Usuario                │
├─────────────────────────────────┤
│ - idUsuario: Integer            │
│ - nombre: String                │
│ - apellido: String              │
│ - correo: String                │
│ - contrasena: String            │
│ - rol: Rol                      │
├─────────────────────────────────┤
│ + registrarse()                 │
│ + autenticarse()                │
│ + actualizarPerfil()            │
└──────────────┬────────────────┬─┘
               │                │
         ┌─────▼──────┐    ┌────▼────────┐
         │ *Reservas  │    │ *Comentarios│
         └────────────┘    └─────────────┘

┌─────────────────────────────────┐
│          Reserva                │
├─────────────────────────────────┤
│ - idReserva: Integer            │
│ - fechaReserva: Date            │
│ - horaEntrada: LocalTime        │
│ - horaSalida: LocalTime         │
│ - precioTotal: Double           │
│ - fechaCreacion: Date           │
│ - estadoReserva: Enum           │
│ - usuario: Usuario              │
│ - lugar: Lugar                  │
│ - metodoPago: MetodoPago        │
│ - cancha: Cancha                │
├─────────────────────────────────┤
│ + crearReserva()                │
│ + cancelarReserva()             │
│ + calcularPrecio()              │
│ + generarConfirmacion()         │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│          Cancha                 │
├─────────────────────────────────┤
│ - idCancha: Integer             │
│ - nombre: String                │
│ - numeroCancha: Integer         │
│ - imagenes: List<String>        │
│ - tipoCancha: TipoCancha        │
│ - lugar: Lugar                  │
├─────────────────────────────────┤
│ + obtenerDisponibilidad()       │
│ + obtenerJornadas()             │
│ + actualizarCancha()            │
└──────────────┬────────────────┬─┘
               │                │
         ┌─────▼──────┐    ┌────▼────────┐
         │ *Jornadas  │    │ *Reservas   │
         └────────────┘    └─────────────┘
```

### 5. Diagrama de Estado - Estados de Reserva

```
              ┌────────────────┐
              │   PENDIENTE    │
              └────────┬───────┘
                       │
                       │ Confirmar pago
                       ▼
              ┌────────────────┐
              │   CONFIRMADA   │◄─────────────┐
              └────────┬───────┘              │
                       │                     │
         ┌─────────────┴─────────────┐      │
         │                           │      │
         │ Ejecutar                  │ Reintentar
         ▼                           │      │
    ┌────────────┐           ┌───────┴──────┴┐
    │ FINALIZADA │           │   RECHAZADA  │
    └────────────┘           └──────────────┘
         │
         │ Cancelar
         ▼
    ┌────────────┐
    │ CANCELADA  │
    └────────────┘
```

---

##  Documentación de Procesos

### 1. Proceso de Autenticación y Registro

#### 1.1 Registro de Usuario Nuevo

**Actores:** Usuario no autenticado

**Precondiciones:**
- El usuario tiene acceso a la aplicación
- El usuario no posee una cuenta registrada

**Flujo Principal:**
1. Usuario accede a la pantalla de registro
2. Usuario ingresa: nombre, apellido, correo, contraseña
3. Sistema valida los datos
4. Sistema verifica que el correo no esté registrado
5. Sistema encripta la contraseña
6. Sistema asigna rol por defecto (USUARIO)
7. Sistema guarda el usuario en base de datos
8. Sistema genera token JWT
9. Usuario es redirigido al dashboard

**Flujos Alternativos:**
- Si el correo ya existe: Mostrar error "Correo ya registrado"
- Si datos son inválidos: Mostrar validaciones específicas

#### 1.2 Autenticación de Usuario

**Actores:** Usuario registrado

**Precondiciones:**
- Usuario tiene cuenta activa
- Usuario posee credenciales válidas

**Flujo Principal:**
1. Usuario accede a login
2. Usuario ingresa correo y contraseña
3. Sistema busca usuario por correo
4. Sistema verifica contraseña
5. Sistema genera token JWT
6. Sistema retorna token al cliente
7. Usuario accede a aplicación

**Validaciones:**
- Correo debe existir en sistema
- Contraseña debe coincidir (hasheada)
- Token debe ser válido por 24 horas

### 2. Proceso de Creación de Reserva

**Actores:** Usuario autenticado

**Precondiciones:**
- Usuario está autenticado
- Cancha está disponible en fecha/horario solicitado
- Usuario tiene método de pago registrado

**Flujo Principal:**
1. Usuario selecciona una cancha
2. Usuario selecciona fecha y horario
3. Sistema valida disponibilidad
4. Sistema calcula precio total
5. Usuario selecciona método de pago
6. Usuario revisa detalles de reserva
7. Usuario confirma reserva
8. Sistema procesa pago (simulado)
9. Sistema crea registro de reserva
10. Sistema envía confirmación por correo
11. Sistema retorna confirmación a usuario

**Cálculo de Precio:**
```
Precio Total = (Tarifa Hora × Cantidad Horas) + Impuestos
Cantidad Horas = HoraSalida - HoraEntrada
Tarifa Hora = tipoCancha.precio
```

**Validaciones Críticas:**
- ✓ Cancha debe estar disponible en fecha/horario
- ✓ Usuario no puede tener más de 3 reservas activas simultáneamente
- ✓ Horario debe estar dentro de jornadas registradas
- ✓ Pago debe procesarse exitosamente
- ✓ No se permite reservar en fechas pasadas

**Flujos Alternativos:**
- Si cancha no está disponible: Mostrar horarios disponibles alternos
- Si el pago falla: Permitir reintentar con otro método
- Si usuario excede límite de reservas: Mostrar error y opciones

### 3. Proceso de Cancelación de Reserva

**Actores:** Usuario autenticado, Administrador

**Precondiciones:**
- Reserva existe y está en estado PENDIENTE o CONFIRMADA
- Usuario es propietario de la reserva o es administrador

**Flujo Principal:**
1. Usuario/Admin selecciona reserva a cancelar
2. Sistema valida que pueda ser cancelada
3. Usuario/Admin confirma cancelación
4. Sistema cambia estado a CANCELADA
5. Sistema registra fecha de cancelación
6. Sistema procesa reembolso (si aplica)
7. Sistema envía confirmación por correo
8. Reserva es removida de disponibilidad

**Políticas de Reembolso:**
- Si cancela 24h antes: 100% reembolso
- Si cancela 12h antes: 50% reembolso
- Si cancela menos de 12h: 0% reembolso

### 4. Proceso de Búsqueda y Filtrado de Canchas

**Actores:** Usuario autenticado

**Flujo Principal:**
1. Usuario accede a catálogo de canchas
2. Usuario aplica filtros:
   - Zona/Ubicación
   - Tipo de cancha
   - Fecha disponible
   - Rango horario
3. Sistema ejecuta búsqueda con criterios
4. Sistema valida disponibilidad en cada cancha
5. Sistema retorna listado ordenado por relevancia
6. Usuario selecciona una cancha
7. Sistema muestra detalles y horarios disponibles

**Criterios de Ordenamiento:**
- Proximidad a ubicación
- Calificación
- Precio
- Disponibilidad

---

##  Documentación de Dominio

### 1. Glosario de Términos

| Término | Definición |
|---------|-----------|
| **Usuario** | Persona registrada en el sistema que puede realizar reservas |
| **Cancha** | Instalación deportiva disponible para reserva |
| **Tipo Cancha** | Clasificación de canchas (futbol, basquetbol, tenis, etc.) |
| **Jornada** | Período de tiempo dentro de un día en el que una cancha está disponible |
| **Reserva** | Registro de uso de cancha por un usuario en fecha/hora específica |
| **Zona** | Área geográfica donde se ubican las canchas |
| **Lugar** | Ubicación específica que contiene una o más canchas |
| **Método de Pago** | Forma de pago aceptada (tarjeta, efectivo, transferencia) |
| **Rol** | Conjunto de permisos asignados a un usuario (USUARIO, ADMIN) |
| **Estado Reserva** | Situación actual de una reserva (PENDIENTE, CONFIRMADA, CANCELADA, FINALIZADA) |

### 2. User Stories Mapeadas a Funcionalidades

#### Historia 1: Registro e Inicio de Sesión
```
Como: Usuario nuevo
Quiero: Crear una cuenta en la plataforma
Para que: Pueda acceder y realizar reservas

Criterios de Aceptación:
- [ ] Puedo registrarme con correo y contraseña
- [ ] Mi contraseña es almacenada de forma segura (hasheada)
- [ ] Recibo confirmación de registro
- [ ] Puedo iniciar sesión inmediatamente después
- [ ] Sistema valida formato de correo
- [ ] Sistema rechaza contraseñas débiles
```

#### Historia 2: Buscar Canchas Disponibles
```
Como: Usuario autenticado
Quiero: Buscar canchas disponibles por zona y tipo
Para que: Encuentre opciones que se ajusten a mis necesidades

Criterios de Aceptación:
- [ ] Puedo filtrar por zona geográfica
- [ ] Puedo filtrar por tipo de cancha
- [ ] Puedo filtrar por fecha disponible
- [ ] Puedo filtrar por rango horario
- [ ] Los resultados muestran precio, ubicación y disponibilidad
- [ ] Puedo ordenar resultados por relevancia
```

#### Historia 3: Realizar Reserva
```
Como: Usuario autenticado
Quiero: Reservar una cancha para una fecha/hora específica
Para que: Asegure su disponibilidad

Criterios de Aceptación:
- [ ] Puedo seleccionar fecha y horario disponibles
- [ ] Se calcula el precio automáticamente
- [ ] Puedo seleccionar método de pago
- [ ] Sistema valida datos antes de procesar
- [ ] Recibo confirmación de reserva por correo
- [ ] Mi reserva aparece en "Mis Reservas"
- [ ] No puedo hacer más de 3 reservas simultáneas
```

#### Historia 4: Cancelar Reserva
```
Como: Usuario autenticado
Quiero: Cancelar una de mis reservas
Para que: Libere la cancha si cambio de planes

Criterios de Aceptación:
- [ ] Puedo cancelar solo mis propias reservas
- [ ] Puedo ver política de reembolso antes de cancelar
- [ ] Sistema procesa reembolso según política
- [ ] Recibo confirmación de cancelación
- [ ] Cancha se libera inmediatamente
```

#### Historia 5: Gestión de Usuarios (Admin)
```
Como: Administrador
Quiero: Gestionar usuarios del sistema
Para que: Controle acceso y permisos

Criterios de Aceptación:
- [ ] Puedo listar todos los usuarios
- [ ] Puedo asignar roles a usuarios
- [ ] Puedo suspender/reactivar usuarios
- [ ] Puedo ver historial de actividad
- [ ] Sistema registra cambios realizados
```

### 3. Casos de Negocio / Escenarios

#### Escenario 1: Reserva en Hora Pico
```
Contexto: Viernes a las 18:00hs
Usuario: Juan quiere jugar futbol

Flujo:
1. Juan busca canchas disponibles para hoy
2. Encuentra 2 canchas disponibles
3. Selecciona la más cercana a su ubicación
4. Elige horario 19:00-20:00
5. Sistema valida disponibilidad
6. Juan realiza pago
7. Se genera confirmación
8. Cancha se marca como no disponible en ese horario
```

#### Escenario 2: Cancelación por Emergencia
```
Contexto: Usuario realizó reserva para mañana
Usuario: Pedro necesita cancelar por emergencia

Flujo:
1. Pedro accede a "Mis Reservas"
2. Selecciona reserva de mañana
3. Hace click en "Cancelar Reserva"
4. Sistema muestra política de reembolso (100%)
5. Pedro confirma cancelación
6. Sistema procesa reembolso a su cuenta
7. Cancha vuelve a disponibilidad
8. Otros usuarios pueden ver slot nuevamente
```

#### Escenario 3: Búsqueda Inteligente
```
Contexto: Martes por la tarde
Usuario: María busca cancha para jugar basquetbol

Flujo:
1. María accede a catálogo
2. Filtra por "Basquetbol"
3. Filtra por zona "Centro"
4. Filtra por horario "18:00-20:00"
5. Filtra por disponibilidad próximos 7 días
6. Sistema retorna 3 opciones
7. María compara precios
8. María elige la más económica
9. Realiza reserva con promoción disponible
```

---

##  Configuración y Ejecución

### Requisitos Previos

- **Java:** JDK 21+
- **PostgreSQL:** 12.0+
- **Maven:** 3.8.0+
- **Docker:** (Opcional, para despliegue containerizado)

### Variables de Entorno

Crear archivo `.env` en la raíz del proyecto:

```env
# Base de Datos
DATABASE_URL=jdbc:postgresql://localhost:5432/canchitas_management
PGUSER=postgres
PGPASSWORD=admin

# Servidor
PORT=8080

# Seguridad
JWT_SECRET=your_secret_key_here_change_in_production
JWT_EXPIRATION=86400000

# Logs
SHOW_SQL=false
LOGGING_LEVEL=INFO
```

### Instalación Local

1. **Clonar repositorio:**
```bash
git clone https://github.com/FabrizioCalderonn/sportmatch-backend.git
cd Canchitas
```

2. **Instalar dependencias:**
```bash
mvn clean install
```

3. **Crear base de datos:**
```sql
CREATE DATABASE canchitas_management;
```

4. **Ejecutar aplicación:**
```bash
mvn spring-boot:run
```

5. **Verificar que esté ejecutándose:**
```bash
curl http://localhost:8080/health
```

### Despliegue con Docker

1. **Buildear imagen:**
```bash
docker build -t sportmatch-backend:latest .
```

2. **Ejecutar contenedor:**
```bash
docker run -d \
  --name sportmatch-backend \
  -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/canchitas_management \
  -e PGUSER=postgres \
  -e PGPASSWORD=admin \
  sportmatch-backend:latest
```

---

##  Endpoints API

### 1. Autenticación

#### Registrar Usuario
```http
POST /api/auth/register
Content-Type: application/json

{
  "nombre": "Juan",
  "apellido": "Pérez",
  "correo": "juan@example.com",
  "contrasena": "Password123!",
  "confirmarContrasena": "Password123!"
}

Respuesta: 201 Created
{
  "mensaje": "Usuario registrado exitosamente",
  "idUsuario": 1,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Iniciar Sesión
```http
POST /api/auth/login
Content-Type: application/json

{
  "correo": "juan@example.com",
  "contrasena": "Password123!"
}

Respuesta: 200 OK
{
  "mensaje": "Inicio de sesión exitoso",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "idUsuario": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "correo": "juan@example.com",
    "rol": "USUARIO"
  }
}
```

### 2. Canchas

#### Obtener Todas las Canchas
```http
GET /api/canchas
Authorization: Bearer {token}

Parámetros:
- tipo: string (futbol, basquetbol, etc.)
- zona: string
- disponible: boolean

Respuesta: 200 OK
[
  {
    "idCancha": 1,
    "nombre": "Cancha 1",
    "numeroCancha": 1,
    "tipoCancha": {
      "idTipoCancha": 1,
      "nombre": "Futbol",
      "precio": 50.00
    },
    "lugar": {
      "idLugar": 1,
      "nombre": "Centro Sports Club",
      "direccion": "Calle Principal 123"
    },
    "imagenes": ["url1", "url2"],
    "disponible": true
  }
]
```

#### Obtener Cancha por ID
```http
GET /api/canchas/{idCancha}
Authorization: Bearer {token}

Respuesta: 200 OK
{
  "idCancha": 1,
  "nombre": "Cancha 1",
  ...
}
```

### 3. Reservas

#### Crear Reserva
```http
POST /api/reservas
Authorization: Bearer {token}
Content-Type: application/json

{
  "fechaReserva": "2025-01-15",
  "horaEntrada": "19:00",
  "horaSalida": "20:30",
  "idCancha": 1,
  "idLugar": 1,
  "idMetodoPago": 1
}

Respuesta: 201 Created
{
  "idReserva": 1,
  "fechaReserva": "2025-01-15",
  "horaEntrada": "19:00",
  "horaSalida": "20:30",
  "precioTotal": 75.00,
  "estadoReserva": "PENDIENTE",
  "fechaCreacion": "2024-11-26T10:30:00"
}
```

#### Obtener Mis Reservas
```http
GET /api/reservas/mis-reservas
Authorization: Bearer {token}

Parámetros:
- estado: PENDIENTE|CONFIRMADA|CANCELADA|FINALIZADA
- fechaDesde: 2025-01-01
- fechaHasta: 2025-12-31

Respuesta: 200 OK
{
  "total": 5,
  "reservas": [...]
}
```

#### Cancelar Reserva
```http
DELETE /api/reservas/{idReserva}
Authorization: Bearer {token}

Respuesta: 200 OK
{
  "mensaje": "Reserva cancelada exitosamente",
  "reembolso": 75.00,
  "idReserva": 1
}
```

### 4. Usuarios

#### Obtener Mi Perfil
```http
GET /api/usuarios/perfil
Authorization: Bearer {token}

Respuesta: 200 OK
{
  "idUsuario": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "correo": "juan@example.com",
  "rol": "USUARIO"
}
```

### 5. Catálogo

#### Obtener Tipos de Cancha
```http
GET /api/catalogo/tipos-cancha

Respuesta: 200 OK
[
  { "idTipoCancha": 1, "nombre": "Futbol", "precio": 50.00 },
  { "idTipoCancha": 2, "nombre": "Basquetbol", "precio": 40.00 }
]
```

#### Obtener Zonas
```http
GET /api/catalogo/zonas

Respuesta: 200 OK
[
  { "idZona": 1, "nombre": "Centro" },
  { "idZona": 2, "nombre": "Norte" }
]
```

### 6. Health Check

#### Estado de la Aplicación
```http
GET /health

Respuesta: 200 OK
{
  "status": "UP",
  "database": "UP",
  "timestamp": "2024-11-26T10:30:00"
}
```

---

##  Modelo de Datos

### Entidades Principales

#### Usuario
- `idUsuario` (PK): Integer
- `nombre`: String (100)
- `apellido`: String (100)
- `correo`: String (150, UNIQUE)
- `contrasena`: String (255)
- `idRol` (FK): Integer
- `fechaCreacion`: Timestamp
- `activo`: Boolean

#### Reserva
- `idReserva` (PK): Integer
- `fechaReserva`: Date
- `horaEntrada`: LocalTime
- `horaSalida`: LocalTime
- `precioTotal`: Decimal(10,2)
- `fechaCreacion`: Timestamp
- `estadoReserva`: Enum (PENDIENTE, CONFIRMADA, CANCELADA, FINALIZADA)
- `idUsuario` (FK): Integer
- `idLugar` (FK): Integer
- `idMetodoPago` (FK): Integer
- `idCancha` (FK): Integer

#### Cancha
- `idCancha` (PK): Integer
- `nombre`: String (100)
- `numeroCancha`: Integer
- `idTipoCancha` (FK): Integer
- `idLugar` (FK): Integer
- `imagenes`: List<String>
- `activo`: Boolean

#### Jornada
- `idJornada` (PK): Integer
- `horaInicio`: LocalTime
- `horaFin`: LocalTime
- `idCancha` (FK): Integer
- `idSemana` (FK): Integer
- `activa`: Boolean

#### Lugar
- `idLugar` (PK): Integer
- `nombre`: String (100)
- `direccion`: String (255)
- `idZona` (FK): Integer

#### TipoCancha
- `idTipoCancha` (PK): Integer
- `nombre`: String (100)
- `precio`: Decimal(10,2)

---

##  Reglas de Negocio

### 1. Gestión de Usuarios

| Regla | Descripción | Validación |
|-------|-------------|-----------|
| **RN-001** | Un usuario solo puede tener una cuenta activa | Correo único en sistema |
| **RN-002** | Las contraseñas deben tener mínimo 8 caracteres | 1 mayúscula, 1 minúscula, 1 número, 1 especial |
| **RN-003** | Todo usuario registrado tiene rol "USUARIO" por defecto | Asignación automática al registro |
| **RN-004** | Solo administradores pueden crear otros administradores | Validación en controlador |
| **RN-005** | Usuario no puede cambiar su rol | Prohibido en servicios |

### 2. Gestión de Reservas

| Regla | Descripción | Validación |
|-------|-------------|-----------|
| **RN-006** | Un usuario no puede tener más de 3 reservas activas | Validación antes de crear |
| **RN-007** | Una cancha no puede tener dos reservas superpuestas | Verificación de horarios |
| **RN-008** | No se permite reservar en fechas pasadas | Comparación con fecha actual |
| **RN-009** | El horario de salida debe ser posterior al de entrada | Validación lógica |
| **RN-010** | Los horarios deben coincidir con jornadas registradas | Verificación contra tabla jornadas |
| **RN-011** | Solo se pueden cancelar reservas PENDIENTE/CONFIRMADA | Validación de estado |

### 3. Cálculo de Precios

| Regla | Descripción | Fórmula |
|-------|-------------|---------|
| **RN-012** | El precio se calcula por hora | Tarifa × Horas |
| **RN-013** | Se aplican impuestos al precio | Precio × (1 + %Impuesto) |
| **RN-014** | Se pueden aplicar descuentos | PrecioFinal = PrecioBase - Descuento |
| **RN-015** | El precio debe ser mayor a cero | Validación numérica |

### 4. Reembolsos

| Regla | Descripción | Porcentaje |
|-------|-------------|-----------|
| **RN-016** | Cancelación 24h+ anticipación | 100% reembolso |
| **RN-017** | Cancelación 12-24h anticipación | 50% reembolso |
| **RN-018** | Cancelación menos de 12h | 0% reembolso |

### 5. Seguridad

| Regla | Descripción | Implementación |
|-------|-------------|-----------------|
| **RN-020** | Todas las contraseñas deben ser hasheadas | bcrypt o similar |
| **RN-021** | Token JWT tiene validez de 24 horas | Expiración automática |
| **RN-022** | Cada endpoint requiere autenticación | @Secured en controladores |
| **RN-023** | Admin accede a todos los recursos | Validación por rol |
| **RN-024** | Usuario solo ve/modifica sus datos | Validación de propiedad |

---

##  Información del Proyecto

**Repositorio:** https://github.com/FabrizioCalderonn/sportmatch-backend  
**Rama Principal:** main  
**Última actualización:** 26 de Noviembre de 2024  
**Versión:** 0.0.1 - SNAPSHOT
=======
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
>>>>>>> a8ae85c2771d55eddde289a0fce4ad28b3b5329a
