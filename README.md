# BusTracking · Seguimiento de autobuses de Sevilla en tiempo real

[![CI](https://github.com/Luis-matador/BusTracking/actions/workflows/ci.yml/badge.svg)](https://github.com/Luis-matador/BusTracking/actions/workflows/ci.yml)

Aplicación full stack que simula una flota de autobuses urbanos de Sevilla y muestra su posición en un mapa en tiempo real. El backend calcula el movimiento de cada autobús entre paradas y envía las posiciones al navegador por WebSocket cada pocos segundos.

## Funcionalidades

- **Mapa en tiempo real** con Leaflet: posición, velocidad y próxima parada de cada autobús.
- **Simulación de movimiento**: cada autobús recorre su ruta a ~30 km/h (±20 %), interpolando su posición entre paradas con la fórmula de Haversine.
- **WebSocket (STOMP + SockJS)**: el servidor publica las posiciones en `/topic/posiciones-buses` y en un canal por autobús.
- **Gestión de flota**: API REST para autobuses, conductores, rutas y paradas.
- **Usuarios**: registro e inicio de sesión validados en el servidor, con contraseñas cifradas con BCrypt.

## Stack

| Capa | Tecnologías |
| --- | --- |
| Backend | Java 17, Spring Boot 3, Spring Web, Spring Data JPA (Hibernate), Spring WebSocket, Maven |
| Frontend | Angular 19, TypeScript, Leaflet, STOMP.js / SockJS |
| Base de datos | MySQL 8 (H2 en memoria para los tests) |
| Calidad | JUnit 5, Mockito, MockMvc, GitHub Actions |

## Arquitectura del backend

```
controladores/   REST y WebSocket (sin lógica de negocio)
servicios/       interfaces + implementaciones con la lógica
repositorios/    Spring Data JPA
modelo/          entidades JPA, DTOs y estado de la simulación
excepciones/     excepciones de negocio + @RestControllerAdvice que las traduce a 400/401/404/409
```

## Puesta en marcha

### 1. Base de datos

Ejecuta `bus_tracking.sql` en MySQL. Crea la base de datos y un usuario `bus_admin`; cambia antes su contraseña en el script. Después ejecuta los datos de ejemplo de `busTracking/src/main/resources/inserts.txt`.

### 2. Backend

Las credenciales se leen de variables de entorno:

| Variable | Descripción | Por defecto |
| --- | --- | --- |
| `DB_URL` | URL JDBC | `jdbc:mysql://localhost:3306/bus_tracking` |
| `DB_USERNAME` | Usuario de MySQL | `bus_admin` |
| `DB_PASSWORD` | Contraseña de MySQL | (vacía) |
| `ADMIN_CODIGO` | Código para registrarse como administrador | (vacío: nadie puede) |

```bash
cd busTracking
DB_PASSWORD=tu_contraseña mvn spring-boot:run
```

La API queda en `http://localhost:8080/api`.

### 3. Frontend

```bash
cd bus-tracking-app
npm install
npx ng serve
```

Abre `http://localhost:4200`. Usuarios de ejemplo: `juan.perez@example.com` / `demo1234` (y el resto de `inserts.txt`, con la misma contraseña).

## API principal

| Método | Ruta | Descripción |
| --- | --- | --- |
| POST | `/api/auth/registro` | Alta de usuario |
| POST | `/api/auth/login` | Inicio de sesión |
| GET | `/api/buses` | Autobuses de la flota |
| GET | `/api/buses/{id}/destino` | Próxima parada de un autobús |
| GET | `/api/rutas` | Rutas con sus paradas |
| GET | `/api/conductores` | Conductores |
| POST | `/api/simulacion/iniciar/{busId}` | Inicia la simulación de un autobús |
| DELETE | `/api/simulacion/detener/{busId}` | Detiene la simulación de un autobús |

## Tests

```bash
cd busTracking
mvn test
```

Incluye tests unitarios del servicio de usuarios (Mockito), tests del controlador de autenticación (MockMvc), tests de la lógica de simulación y un test de arranque del contexto con H2. GitHub Actions los ejecuta en cada push y compila también el frontend.
