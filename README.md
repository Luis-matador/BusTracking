# Sistema de Seguimiento de Buses de Sevilla

## Requisitos Previos

Antes de iniciar el proyecto, asegúrate de tener instalado:

- Java 17 o superior
- MySQL 8.0 o superior
- Node.js y NPM
- Maven
- Angular CLI (`npm install -g @angular/cli`)

## Configuración Inicial

### 1. Configuración de la Base de Datos

- Ejecuta el script SQL para crear la base de datos y el usuario
- Copia el contenido de inserts.txt y pegalo en la base de datos para insertar los datos.

## Tecnologías Utilizadas

### Backend
- **Java 17**: Lenguaje de programación principal
- **Spring Boot**: Framework para desarrollo de aplicaciones
- **Spring Data JPA**: Persistencia y manejo de datos
- **Spring WebSocket**: Comunicación en tiempo real
- **Maven**: Gestión de dependencias y construcción del proyecto
- **MySQL**: Base de datos relacional

### Frontend
- **Angular**: Framework para el desarrollo del cliente web
- **TypeScript**: Lenguaje de programación tipado
- **Node.js**: Entorno de ejecución para desarrollo
- **npm**: Gestor de paquetes
- **SockJS**: Cliente WebSocket
- **Bootstrap**: Framework CSS para diseño responsive

### Herramientas de Desarrollo
- **IntelliJ IDEA**: IDE principal para desarrollo backend
- **VS Code**: Editor para desarrollo frontend
- **MySQL Workbench**: Gestión de base de datos
- **Postman**: Pruebas de API REST
- **Git**: Control de versiones

### APIs y Servicios
- **REST API**: Comunicación cliente-servidor
- **WebSocket**: Actualizaciones en tiempo real
- **JPA/Hibernate**: Mapeo objeto-relacional
- **Jackson**: Serialización/deserialización JSON

## Estructura del Backend

El sistema está organizado en capas para una mejor mantenibilidad:

### Configuración (`config/`)
- `JacksonConfig`: Maneja la serialización de objetos JSON y evita referencias circulares
- `WebConfig`: Configura CORS y endpoints web para la comunicación con el frontend
- `WebSocketConfig`: Habilita comunicación en tiempo real para actualización de posiciones GPS

### Controladores (`controladores/`)
Endpoints REST para:
- `BusControlador`: Gestión de la flota de autobuses
- `ConductorControlador`: Administración del personal
- `RutaControlador`: Control de líneas y trayectos
- `ParadaControlador`: Gestión de puntos de parada
- `GPSDataControlador`: Recepción de datos de localización
- `SimulacionControlador`: Control de la simulación del sistema
- `WebSocketControlador`: Envío de actualizaciones en tiempo real

### DTOs (`dto/`)
Objetos de transferencia de datos:
- `GPSDataDTO`: Optimiza la transferencia de datos GPS

### Entidades (`entidades/`)
Modelos de datos principales:
- `Bus`: Detalles del vehículo (capacidad, marca, matrícula, ruta asignada)
- `Conductor`: Información del personal (DNI, nombre, apellido, teléfono, bus asignado)
- `Ruta`: Trayectos definidos (nombre, información, paradas)
- `Parada`: Ubicaciones con coordenadas GPS (latitud, longitud, nombre, orden en ruta)
- `GPSData`: Datos de localización en tiempo real

### Servicios (`servicios/`)
Implementan la lógica:
- `BusServicio`: Gestión de buses y asignaciones
- `ConductorServicio`: Administración de conductores
- `RutaServicio`: Control de rutas y trayectos
- `ParadaServicio`: Gestión de paradas
- `GPSDataServicio`: Procesamiento de datos de localización
- `SimulacionServicio`: Control de la simulación del sistema

### Repositorios (`repositorios/`)
Interfaces para acceso a datos mediante JPA:
- `BusRepositorio`: Operaciones CRUD para buses
- `ConductorRepositorio`: Gestión de datos de conductores
- `RutaRepositorio`: Acceso a rutas
- `ParadaRepositorio`: Manejo de paradas
- `GPSDataRepositorio`: Almacenamiento de datos GPS

# Funcionamiento del Sistema de Seguimiento de Buses

## 1. Sistema de Simulación de Movimiento

- Al iniciar la aplicación, se activa automáticamente la simulación para todos los buses
- Cada bus sigue una ruta predefinida entre paradas
- La simulación se ejecuta en ciclos de x segundos
- Velocidad simulada: 30 km/h ± 20% de variación aleatoria
- Cálculo de posición mediante interpolación entre paradas
- La dirección se calcula según el rumbo entre paradas

## 2. Gestión de Datos GPS
- Cada actualización genera un nuevo registro GPS con:
    - Coordenadas (latitud/longitud)
    - Velocidad actual
    - Dirección (0-360 grados)
    - Timestamp
    - Identificación del bus
- Los datos se validan antes de almacenarse:
    - Latitud: -90° a 90°
    - Longitud: -180° a 180°
    - Velocidad: valores positivos
    - Dirección: 0° a 360°
- Limpieza automática de datos GPS cada 30 segundos

## 3. Comunicación en Tiempo Real
- WebSocket envía actualizaciones cada x segundos
- Dos tipos de canales:
    - `/topic/posiciones-buses`: Todas las posiciones
    - `/topic/posicion-bus/{busId}`: Posición específica
- Se utilizan DTOs optimizados para la transferencia:
    - Datos GPS básicos
    - Información reducida del bus
    - Nombre de ruta simplificado

## 4. Control de la Simulación
- Endpoints REST para gestión:
    - Iniciar simulación por bus
    - Detener simulación por bus
    - Detener toda la simulación
- Estado de simulación por bus:
    - Progreso entre paradas (0-100%)
    - Parada actual y siguiente
    - Detección de llegada a paradas

## Anotaciones Utilizadas

### JPA (Persistencia)
- `@Entity`: Define una clase como entidad
- `@Id`: Marca un campo como clave primaria
- `@GeneratedValue`: Genera valores para la clave primaria
- `@ManyToOne`: Define una relación muchos a uno

### Spring MVC
- `@RestController`: Define un controlador REST
- `@Controller`: Define un controlador tradicional
- `@RequestMapping`: Define la ruta base de los endpoints
- `@GetMapping`: Define un endpoint para peticiones GET
- `@PostMapping`: Define un endpoint para peticiones POST
- `@PutMapping`: Define un endpoint para peticiones PUT
- `@DeleteMapping`: Define un endpoint para peticiones DELETE
- `@PathVariable`: Obtiene variables de la URL
- `@RequestBody`: Obtiene datos del cuerpo de la petición
- `@Autowired`: Inyecta dependencias automáticamente

### Transacciones y Programación
- `@Transactional`: Gestiona transacciones de base de datos
- `@Service`: Marca una clase como servicio
- `@Repository`: Marca una clase como repositorio
- `@Scheduled`: Programa la ejecución periódica
- `@Configuration`: Define una clase de configuración
- `@Bean`: Define un bean de Spring
- `@EventListener`: Escucha eventos de Spring

### WebSocket
- `@MessageMapping`: Define un endpoint para mensajes WebSocket
- `@SendTo`: Especifica el destino de los mensajes
- `@EnableWebSocketMessageBroker`: Habilita el broker de mensajes
- `@DestinationVariable`: Obtiene variables de la ruta de destino

### Base de Datos
- `@Query`: Define consultas personalizadas
- `@Param`: Define parámetros en consultas

### Jackson (JSON)
- `@JsonIgnoreProperties`: Ignora propiedades específicas al serializar/deserializar JSON
- `@JsonIgnore`: Excluye un campo de la serialización/deserialización JSON