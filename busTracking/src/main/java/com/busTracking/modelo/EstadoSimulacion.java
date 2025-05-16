package com.busTracking.modelo;

import com.busTracking.entidades.Bus;
import com.busTracking.entidades.Parada;

import java.util.List;


public class EstadoSimulacion {
    private final Bus bus;
    private final List<Parada> paradas;
    private int indiceParadaActual = 0;
    private double progresoSegmento = 0.0; // 0.0 a 1.0

    public EstadoSimulacion(Bus bus, List<Parada> paradas) {
        this.bus = bus;
        this.paradas = paradas;
    }


    public void actualizarProgreso(int segundosTranscurridos, double velocidadPromedio) {
        Parada actual = getParadaActual();
        Parada siguiente = getParadaSiguiente();

        double distanciaKm = calcularDistanciaKm(
                actual.getLatitud(), actual.getLongitud(),
                siguiente.getLatitud(), siguiente.getLongitud());

        double tiempoHoras = distanciaKm / velocidadPromedio;

        double tiempoSegundos = tiempoHoras * 3600;

        progresoSegmento += segundosTranscurridos / tiempoSegundos;

        if (progresoSegmento >= 1.0) {
            indiceParadaActual = (indiceParadaActual + 1) % paradas.size();
            progresoSegmento = 0.0;
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