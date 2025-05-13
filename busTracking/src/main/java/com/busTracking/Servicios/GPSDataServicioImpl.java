package com.busTracking.Servicios;

import com.busTracking.entidades.GPSData;
import com.busTracking.repositorios.GPSDataRepositorio;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GPSDataServicioImpl implements GPSDataServicio{

    private final GPSDataRepositorio gpsDataRepositorio;

    public GPSDataServicioImpl(GPSDataRepositorio gpsDataRepositorio) {
        this.gpsDataRepositorio = gpsDataRepositorio;
    }

    @Override
    public GPSData guardarGPSData(GPSData gpsData) {
        return gpsDataRepositorio.save(gpsData);
    }

    @Override
    public GPSData obtenerGPSDataPorId(Long id) {
        return gpsDataRepositorio.findById(id).orElseThrow(() -> new RuntimeException("GPSData con id " + id + " no encontrada"));
    }

    @Override
    public List<GPSData> obtenerTodosLosGPSData() {
        return gpsDataRepositorio.findAll();
    }

    @Override
    public GPSData obtenerUltimoGPSDataPorBus(Long busId) {
        return gpsDataRepositorio.findLastGPSDataForBus(busId);
    }

    @Override
    public List<GPSData> obtenerGPSDataPorBusOrdenadoDesc(Long busId) {
        return gpsDataRepositorio.findByBusIdOrderByTiempoDesc(busId);
    }

    @Override
    public List<GPSData> obtenerGPSDataPorBusEnRangoDeTiempo(Long busId, LocalDateTime inicio, LocalDateTime fin) {
        return gpsDataRepositorio.findGPSDataByBusInTimeRange(busId, inicio, fin);
    }

    @Override
    public List<GPSData> obtenerUltimoGPSDataParaTodosLosBuses() {
        return gpsDataRepositorio.findLastGPSDataForAllBuses();
    }
}
