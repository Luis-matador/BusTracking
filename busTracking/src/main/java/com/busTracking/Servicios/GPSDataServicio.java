package com.busTracking.Servicios;

import com.busTracking.entidades.GPSData;

import java.time.LocalDateTime;
import java.util.List;

public interface GPSDataServicio {

    GPSData guardarGPSData(GPSData gpsData);
    GPSData obtenerGPSDataPorId(Long id);
    List<GPSData> obtenerTodosLosGPSData();
    GPSData obtenerUltimoGPSDataPorBus(Long busId);
    List<GPSData> obtenerGPSDataPorBusOrdenadoDesc(Long busId);
    List<GPSData> obtenerGPSDataPorBusEnRangoDeTiempo(Long busId, LocalDateTime inicio, LocalDateTime fin);
    List<GPSData> obtenerUltimoGPSDataParaTodosLosBuses();

}
