package com.busTracking.servicios;

import com.busTracking.entidades.GPSData;
import com.busTracking.repositorios.BusRepositorio;
import com.busTracking.repositorios.GPSDataRepositorio;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GPSDataServicioImpl implements GPSDataServicio {

    private final GPSDataRepositorio gpsDataRepositorio;
    private final BusRepositorio busRepositorio;

    public GPSDataServicioImpl(GPSDataRepositorio gpsDataRepositorio, BusRepositorio busRepositorio) {
        this.gpsDataRepositorio = gpsDataRepositorio;
        this.busRepositorio = busRepositorio;
    }

    @Override
    @Transactional
    public GPSData guardarGPSData(GPSData gpsData) {
        if (gpsData.getBus() != null) {
            Long busId = gpsData.getBus().getId();
            if (busId != null) {
                if (!busRepositorio.existsById(busId)) {
                    throw new EntityNotFoundException("No se encontró bus con ID: " + busId);
                }
            } else {
                throw new IllegalArgumentException("El bus proporcionado no tiene un ID válido");
            }
        }

        if (gpsData.getTiempo() == null) {
            gpsData.setTiempo(LocalDateTime.now());
        }

        validarDatosGPS(gpsData);

        return gpsDataRepositorio.save(gpsData);
    }


    @Override
    public GPSData obtenerGPSDataPorId(Long id) {
        return gpsDataRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("GPSData con id " + id + " no encontrada"));
    }

    @Override
    public List<GPSData> obtenerTodosLosGPSData() {
        return gpsDataRepositorio.findAll();
    }

    @Override
    public GPSData obtenerUltimoGPSDataPorBus(Long busId) {
        if (!busRepositorio.existsById(busId)) {
            throw new EntityNotFoundException("No se encontró bus con ID: " + busId);
        }

        GPSData ultimoGPSData = gpsDataRepositorio.findLastGPSDataForBus(busId);
        if (ultimoGPSData == null) {
            throw new EntityNotFoundException("No hay datos GPS registrados para el bus con ID: " + busId);
        }

        return ultimoGPSData;
    }

    @Override
    public List<GPSData> obtenerGPSDataPorBusOrdenadoDesc(Long busId) {
        if (!busRepositorio.existsById(busId)) {
            throw new EntityNotFoundException("No se encontró bus con ID: " + busId);
        }

        return gpsDataRepositorio.findByBusIdOrderByTiempoDesc(busId);
    }

    @Override
    public List<GPSData> obtenerGPSDataPorBusEnRangoDeTiempo(Long busId, LocalDateTime inicio, LocalDateTime fin) {
        if (!busRepositorio.existsById(busId)) {
            throw new EntityNotFoundException("No se encontró bus con ID: " + busId);
        }

        if (inicio != null && fin != null && inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        return gpsDataRepositorio.findGPSDataByBusInTimeRange(busId, inicio, fin);
    }

    @Override
    public List<GPSData> obtenerUltimoGPSDataParaTodosLosBuses() {
        return gpsDataRepositorio.findLastGPSDataForAllBuses();
    }

    @Transactional
    @Override
    public GPSData actualizarGPSData(Long id, GPSData gpsData) {
        GPSData gpsDataExistente = gpsDataRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GPSData con id " + id + " no encontrada"));

        if (gpsData.getLatitud() != null) {
            gpsDataExistente.setLatitud(gpsData.getLatitud());
        }

        if (gpsData.getLongitud() != null) {
            gpsDataExistente.setLongitud(gpsData.getLongitud());
        }

        if (gpsData.getVelocidad() != null) {
            gpsDataExistente.setVelocidad(gpsData.getVelocidad());
        }

        if (gpsData.getTiempo() != null) {
            gpsDataExistente.setTiempo(gpsData.getTiempo());
        }

        if (gpsData.getBus() != null) {
            Long busId = gpsData.getBus().getId();
            if (busId != null) {
                if (!busRepositorio.existsById(busId)) {
                    throw new EntityNotFoundException("No se encontró bus con ID: " + busId);
                }
                gpsDataExistente.setBus(gpsData.getBus());
            } else {
                throw new IllegalArgumentException("El bus proporcionado no tiene un ID válido");
            }
        }

        validarDatosGPS(gpsDataExistente);

        return gpsDataRepositorio.save(gpsDataExistente);
    }


    @Transactional
    @Override
    public void eliminarGPSData(Long id) {
        if (!gpsDataRepositorio.existsById(id)) {
            throw new EntityNotFoundException("GPSData con id " + id + " no encontrada");
        }
        gpsDataRepositorio.deleteById(id);
    }


    private void validarDatosGPS(GPSData gpsData) {
        if (gpsData.getLatitud() != null && (gpsData.getLatitud() < -90 || gpsData.getLatitud() > 90)) {
            throw new IllegalArgumentException("La latitud debe estar entre -90 y 90 grados");
        }

        if (gpsData.getLongitud() != null && (gpsData.getLongitud() < -180 || gpsData.getLongitud() > 180)) {
            throw new IllegalArgumentException("La longitud debe estar entre -180 y 180 grados");
        }

        if (gpsData.getVelocidad() != null && gpsData.getVelocidad() < 0) {
            throw new IllegalArgumentException("La velocidad no puede ser negativa");
        }
    }
}