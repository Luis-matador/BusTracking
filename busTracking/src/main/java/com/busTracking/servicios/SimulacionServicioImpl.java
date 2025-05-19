package com.busTracking.servicios;

import com.busTracking.dto.GPSDataDTO;
import com.busTracking.entidades.Bus;
import com.busTracking.entidades.GPSData;
import com.busTracking.entidades.Parada;
import com.busTracking.modelo.EstadoSimulacion;
import com.busTracking.repositorios.BusRepositorio;
import com.busTracking.repositorios.ParadaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SimulacionServicioImpl implements SimulacionServicio {

    private final ParadaRepositorio paradaRepositorio;
    private final GPSDataServicio gpsDataServicio;
    private final BusRepositorio busRepositorio;
    private final SimpMessagingTemplate messagingTemplate;

    private final Map<Long, EstadoSimulacion> estadoSimulacionPorBus = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private static final double VELOCIDAD_PROMEDIO = 30.0;
    private static final int INTERVALO_ACTUALIZACION = 3;
    private static final int INTERVALO_ENVIO_WEBSOCKET = 3;
    private static final double FACTOR_VARIACION_VELOCIDAD = 0.2;

    private boolean simulacionActiva = false;

    // Inyección de dependencias mediante constructor
    @Autowired
    public SimulacionServicioImpl(
            ParadaRepositorio paradaRepositorio,
            GPSDataServicio gpsDataServicio,
            BusRepositorio busRepositorio,
            SimpMessagingTemplate messagingTemplate) {
        this.paradaRepositorio = paradaRepositorio;
        this.gpsDataServicio = gpsDataServicio;
        this.busRepositorio = busRepositorio;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            // Esperar un segundo para asegurar que todas las dependencias estén inicializadas
            Thread.sleep(1000);
            // Iniciar automáticamente la simulación para todos los buses al arrancar
            iniciarSimulacionAutomatica();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Error al esperar inicialización: " + e.getMessage());
        }
    }

    /**
     * Iniciar automáticamente la simulación para todos los buses disponibles
     */
    private void iniciarSimulacionAutomatica() {
        try {
            // Verificamos que el repositorio no sea nulo antes de usarlo
            if (busRepositorio == null) {
                System.err.println("ERROR: BusRepositorio no ha sido inyectado correctamente");
                return;
            }

            List<Bus> buses = busRepositorio.findAll();

            if (buses.isEmpty()) {
                System.out.println("ADVERTENCIA: No hay buses disponibles en la base de datos.");
                return;
            }

            System.out.println("Iniciando simulación automática para " + buses.size() + " buses...");

            for (Bus bus : buses) {
                try {
                    iniciarSimulacionParaBus(bus);
                    System.out.println("Simulación iniciada para bus ID: " + bus.getId() + ", Matrícula: " + bus.getMatricula());
                } catch (IllegalStateException e) {
                    System.err.println("No se pudo iniciar la simulación para el bus " + bus.getId() + ": " + e.getMessage());
                }
            }

            // Programar envío periódico de posiciones por WebSocket
            scheduler.scheduleAtFixedRate(this::enviarPosicionesActualesWebSocket,
                    1, INTERVALO_ENVIO_WEBSOCKET, TimeUnit.SECONDS);

        } catch (Exception e) {
            System.err.println("Error al iniciar simulación automática: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void iniciarSimulacionParaBus(Bus bus) throws IllegalStateException {
        if (bus.getRuta() == null) {
            throw new IllegalStateException("El bus no tiene una ruta asignada");
        }

        List<Parada> paradas = paradaRepositorio.findByRutaAsociadaIdOrderByOrdenAsc(bus.getRuta().getId());

        if (paradas.isEmpty()) {
            throw new IllegalStateException("La ruta no tiene paradas asignadas");
        }

        EstadoSimulacion estado = new EstadoSimulacion(bus, paradas);
        estadoSimulacionPorBus.put(bus.getId(), estado);

        if (!simulacionActiva) {
            iniciarActualizacionesSimulacion();
        }

        System.out.println("Simulación iniciada para bus ID: " + bus.getId());
    }

    @Override
    public void detenerSimulacionParaBus(Long busId) {
        estadoSimulacionPorBus.remove(busId);
        System.out.println("Simulación detenida para bus ID: " + busId);

        if (estadoSimulacionPorBus.isEmpty()) {
            detenerSimulacion();
        }
    }

    @Override
    public void detenerSimulacion() {
        simulacionActiva = false;
        scheduler.shutdown();
        estadoSimulacionPorBus.clear();
        System.out.println("Todas las simulaciones han sido detenidas");
    }

    private void iniciarActualizacionesSimulacion() {
        simulacionActiva = true;
        scheduler.scheduleAtFixedRate(this::actualizarTodasLasSimulaciones,
                0, INTERVALO_ACTUALIZACION, TimeUnit.SECONDS);

        System.out.println("Actualizaciones de simulación iniciadas con intervalo de " + INTERVALO_ACTUALIZACION + " segundos");
    }

    private void actualizarTodasLasSimulaciones() {
        int totalBuses = estadoSimulacionPorBus.size();
        int actualizados = 0;

        for (Map.Entry<Long, EstadoSimulacion> entry : estadoSimulacionPorBus.entrySet()) {
            Long busId = entry.getKey();
            EstadoSimulacion estado = entry.getValue();

            try {
                actualizarPosicionBus(estado);
                actualizados++;
            } catch (Exception e) {
                System.err.println("Error actualizando bus " + busId + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Actualizadas posiciones de " + actualizados + "/" + totalBuses + " buses");
    }

    private void actualizarPosicionBus(EstadoSimulacion estado) {
        double[] nuevaPosicion = calcularNuevaPosicion(estado);
        double latitud = nuevaPosicion[0];
        double longitud = nuevaPosicion[1];

        // Calculamos la dirección (rumbo) entre los puntos
        double direccion = calcularDireccion(
                estado.getParadaActual().getLatitud(),
                estado.getParadaActual().getLongitud(),
                estado.getParadaSiguiente().getLatitud(),
                estado.getParadaSiguiente().getLongitud());

        Random random = new Random();
        double variacion = 1.0 + (random.nextDouble() * 2 * FACTOR_VARIACION_VELOCIDAD - FACTOR_VARIACION_VELOCIDAD);
        double velocidad = VELOCIDAD_PROMEDIO * variacion;

        GPSData nuevoGPS = new GPSData();
        nuevoGPS.setLatitud(latitud);
        nuevoGPS.setLongitud(longitud);
        nuevoGPS.setTiempo(LocalDateTime.now());
        nuevoGPS.setVelocidad(velocidad);
        nuevoGPS.setDireccion(direccion);
        nuevoGPS.setBus(estado.getBus());

        // Usamos el servicio en lugar del repositorio
        gpsDataServicio.guardarGPSData(nuevoGPS);

        // Guardar progreso anterior para comparar
        double progresoAnterior = estado.getProgresoSegmento();
        int paradaIndexAnterior = estado.getIndiceParadaActual();

        estado.actualizarProgreso(INTERVALO_ACTUALIZACION, VELOCIDAD_PROMEDIO);

        // Verificamos si ha llegado a una parada comparando antes y después
        if (paradaIndexAnterior != estado.getIndiceParadaActual() ||
                (progresoAnterior >= 0.99 && estado.getProgresoSegmento() < 0.1)) {
            System.out.println("Bus " + estado.getBus().getId() + " ha llegado a la parada: " +
                    estado.getParadaActual().getNombre());
        }
    }

    private double[] calcularNuevaPosicion(EstadoSimulacion estado) {
        Parada paradaActual = estado.getParadaActual();
        Parada paradaSiguiente = estado.getParadaSiguiente();

        double progresoSegmento = estado.getProgresoSegmento();

        double latitud = paradaActual.getLatitud() +
                (paradaSiguiente.getLatitud() - paradaActual.getLatitud()) * progresoSegmento;

        double longitud = paradaActual.getLongitud() +
                (paradaSiguiente.getLongitud() - paradaActual.getLongitud()) * progresoSegmento;

        return new double[] {latitud, longitud};
    }

    private double calcularDireccion(double lat1, double lon1, double lat2, double lon2) {
        double dLon = Math.toRadians(lon2 - lon1);
        double y = Math.sin(dLon) * Math.cos(Math.toRadians(lat2));
        double x = Math.cos(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) -
                Math.sin(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(dLon);
        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    private void enviarPosicionesActualesWebSocket() {
        try {
            List<GPSDataDTO> posicionesDTO = new ArrayList<>();

            for (Long busId : estadoSimulacionPorBus.keySet()) {
                GPSData ultimaPosicion = gpsDataServicio.obtenerUltimaPosicionPorBusId(busId);
                if (ultimaPosicion != null) {
                    posicionesDTO.add(new GPSDataDTO(ultimaPosicion));
                }
            }

            if (!posicionesDTO.isEmpty()) {
                System.out.println("Enviando " + posicionesDTO.size() + " posiciones por WebSocket");
                messagingTemplate.convertAndSend("/topic/posiciones-buses", posicionesDTO);
            } else {
                System.out.println("No hay posiciones para enviar por WebSocket");
                messagingTemplate.convertAndSend("/topic/posiciones-buses", new ArrayList<>());
            }

        } catch (Exception e) {
            System.err.println("Error al enviar posiciones por WebSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }
}