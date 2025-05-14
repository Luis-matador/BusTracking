package com.busTracking.servicios;

import com.busTracking.entidades.GPSData;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    GPSData actualizarGPSData(Long id, GPSData gpsData);

    @Transactional
    void eliminarGPSData(Long id);
}
