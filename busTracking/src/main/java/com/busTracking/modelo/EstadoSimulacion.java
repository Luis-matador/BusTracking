package com.busTracking.modelo;

import com.busTracking.entidades.Bus;
import com.busTracking.entidades.Parada;

import java.util.List;

public class EstadoSimulacion {
    private final Bus bus;
    private final List<Parada> paradas;
    private int indiceParadaActual = 0;
    private double progresoSegmento = 0.0; // 0.0 a 1.0
    private boolean llegadaParada = false;

    public EstadoSimulacion(Bus bus, List<Parada> paradas) {
        this.bus = bus;
        this.paradas = paradas;
    }

    public void actualizarProgreso(int segundos, double velocidadKmh) {
        // Convertir velocidad de km/h a metros/segundo
        double velocidadMs = velocidadKmh / 3.6;

        // Calcular distancia recorrida en este intervalo
        double distanciaRecorrida = velocidadMs * segundos;

        // Obtener distancia total del segmento actual
        double distanciaSegmento = calcularDistanciaEnMetros(
                getParadaActual().getLatitud(), getParadaActual().getLongitud(),
                getParadaSiguiente().getLatitud(), getParadaSiguiente().getLongitud());

        // Actualizar progreso en este segmento
        double progresoAdicional = distanciaRecorrida / distanciaSegmento;
        progresoSegmento += progresoAdicional;

        // Resetear bandera de llegada
        llegadaParada = false;

        // Si hemos completado el segmento, avanzar a la siguiente parada
        if (progresoSegmento >= 1.0) {
            // Avanzar a la siguiente parada
            indiceParadaActual = (indiceParadaActual + 1) % paradas.size();

            // Reiniciar progreso para el nuevo segmento
            progresoSegmento = 0.0;

            // Marcar que hemos llegado a una parada
            llegadaParada = true;
        }
    }

    public Bus getBus() {
        return bus;
    }

    public Parada getParadaActual() {
        return paradas.get(indiceParadaActual);
    }

    public Parada getParadaSiguiente() {
        int indiceSiguiente = (indiceParadaActual + 1) % paradas.size();
        return paradas.get(indiceSiguiente);
    }

    public double getProgresoSegmento() {
        return progresoSegmento;
    }

    public int getIndiceParadaActual() {
        return indiceParadaActual;
    }

    public boolean isLlegadaParada() {
        return llegadaParada;
    }

    public void setLlegadaParada(boolean llegadaParada) {
        this.llegadaParada = llegadaParada;
    }

    private double calcularDistanciaEnMetros(double lat1, double lon1, double lat2, double lon2) {
        // Convertimos la distancia en km a metros multiplicando por 1000
        return calcularDistanciaKm(lat1, lon1, lat2, lon2) * 1000;
    }

    private double calcularDistanciaKm(double lat1, double lon1, double lat2, double lon2) {
        final int RADIO_TIERRA = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return RADIO_TIERRA * c;
    }
}