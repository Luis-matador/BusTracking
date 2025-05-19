package com.busTracking.servicios;

import com.busTracking.entidades.GPSData;

import java.time.LocalDateTime;
import java.util.List;

public interface GPSDataServicio {
    GPSData guardarGPSData(GPSData gpsData);

    GPSData obtenerGPSDataPorId(Long id);

    List<GPSData> obtenerTodosLosGPSData();

    GPSData obtenerUltimoGPSDataPorBus(Long busId);

    GPSData obtenerUltimaPosicionPorBusId(Long busId);

    List<GPSData> obtenerGPSDataPorBusOrdenadoDesc(Long busId);

    List<GPSData> obtenerGPSDataPorBusEnRangoDeTiempo(Long busId, LocalDateTime inicio, LocalDateTime fin);

    List<GPSData> obtenerUltimoGPSDataParaTodosLosBuses();

    GPSData actualizarGPSData(Long id, GPSData gpsData);

    void eliminarGPSData(Long id);

    GPSData obtenerUltimaUbicacionBus(Long busId);


}