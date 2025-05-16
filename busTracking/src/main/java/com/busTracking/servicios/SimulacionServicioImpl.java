package com.busTracking.servicios;

import com.busTracking.entidades.Bus;
import com.busTracking.entidades.GPSData;
import com.busTracking.entidades.Parada;
import com.busTracking.modelo.EstadoSimulacion;
import com.busTracking.repositorios.GPSDataRepositorio;
import com.busTracking.repositorios.ParadaRepositorio;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SimulacionServicioImpl implements SimulacionServicio {


    private ParadaRepositorio paradaRepositorio;


    private GPSDataRepositorio gpsDataRepositorio;

    private final Map<Long, EstadoSimulacion> estadoSimulacionPorBus = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final double VELOCIDAD_PROMEDIO = 30.0;

    private static final int INTERVALO_ACTUALIZACION = 10;

    private static final double FACTOR_VARIACION_VELOCIDAD = 0.2;

    private boolean simulacionActiva = false;

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
    }

    @Override
    public void detenerSimulacionParaBus(Long busId) {
        estadoSimulacionPorBus.remove(busId);

        if (estadoSimulacionPorBus.isEmpty()) {
            detenerSimulacion();
        }
    }

    @Override
    public void detenerSimulacion() {
        simulacionActiva = false;
        scheduler.shutdown();
        estadoSimulacionPorBus.clear();
    }

    private void iniciarActualizacionesSimulacion() {
        simulacionActiva = true;
        scheduler.scheduleAtFixedRate(this::actualizarTodasLasSimulaciones,
                0, INTERVALO_ACTUALIZACION, TimeUnit.SECONDS);
    }


    private void actualizarTodasLasSimulaciones() {
        estadoSimulacionPorBus.forEach((busId, estado) -> {
            try {
                actualizarPosicionBus(estado);
            } catch (Exception e) {
                System.err.println("Error actualizando bus " + busId + ": " + e.getMessage());
            }
        });
    }


    private void actualizarPosicionBus(EstadoSimulacion estado) {
        double[] nuevaPosicion = calcularNuevaPosicion(estado);
        double latitud = nuevaPosicion[0];
        double longitud = nuevaPosicion[1];

        Random random = new Random();
        double variacion = 1.0 + (random.nextDouble() * 2 * FACTOR_VARIACION_VELOCIDAD - FACTOR_VARIACION_VELOCIDAD);
        double velocidad = VELOCIDAD_PROMEDIO * variacion;

        GPSData nuevoGPS = new GPSData();
        nuevoGPS.setLatitud(latitud);
        nuevoGPS.setLongitud(longitud);
        nuevoGPS.setTiempo(LocalDateTime.now());
        nuevoGPS.setVelocidad(velocidad);
        nuevoGPS.setBus(estado.getBus());

        gpsDataRepositorio.save(nuevoGPS);

        estado.actualizarProgreso(INTERVALO_ACTUALIZACION, VELOCIDAD_PROMEDIO);
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
}